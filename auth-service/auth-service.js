// Generiert Valide JTW für Hasura

const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const fs = require('fs');
const low = require('lowdb');
const FileSync = require('lowdb/adapters/FileSync');
const adapter = new FileSync('db.json');
const db = low(adapter);
const crypto = require('crypto');
const secret = 'boxbase';

const privateKey = fs.readFileSync(__dirname + '/private.pem');
app.use(bodyParser.json());
app.get('/', function (req, res) {
  res.send('Hallo Auth Service');
});

app.post('/login', async function (req, res) {
  let password = req.body.password;
  let username = req.body.username;
  answer = await checkIfUserAccountExistsAndPasswordCorrect(username, password);
  if (!answer) {
    res.status(403).send('Password not matching');
    return;
  }

  const tokenData = {
    sub: answer.sub,
    name: answer.name,
    'https://hasura.io/jwt/claims': {
      'x-hasura-allowed-roles': ['user'],
      'x-hasura-default-role': 'user',
      'x-hasura-role': 'user',
      'x-hasura-user-id': answer.uid,
    },
  };
  res.setHeader('Content-Type', 'application/json');
  const token = jwt.sign(tokenData, privateKey, { algorithm: 'RS256' });
  res.send({ jwt: token, id: answer.uid });
});
app.post('/get_package', async function (req, ress) {
  code = await generatCode(10);
  res.send(code);
});

app.listen(3000);

async function checkIfUserAccountExistsAndPasswordCorrect(username, password) {
  userInDb = db.get('users').filter({ username: username }).take(1).value();
  if (Object.keys(userInDb).length === 0) {
    console.log('User not found in Database');
    return false;
  }
  const dbPassword = userInDb[0].password;
  const hashedPassword = hashPassword(password);

  if (dbPassword !== hashedPassword) {
    console.log('Password wrong');
    return false;
  }
  const uid = userInDb[0].id;
  const sub = userInDb[0].id;
  const name = username;
  const answer = {
    sub,
    name,
    uid,
  };
  return answer;
}
async function generatCode(fachnummer) {
  let randomNumber = [];
  randomNumber.push(getDigitAtPos(0, fachnummer));
  randomNumber.push(getDigitAtPos(1, fachnummer));
  for (let index = 0; index < 3; index++) {
    const element = Math.floor(Math.random() * 10);
    randomNumber.push(element);
  }
  return randomNumber;
}
function getDigitAtPos(n, number) {
  // convert number to a string, then extract the first digit
  var one = String(number).charAt(n);

  // convert the first digit back to an integer
  var one_as_number = Number(one);
  return one_as_number;
}
function hashPassword(password) {
  const hash = crypto
    .createHmac('sha256', secret)
    .update(password)
    .digest('hex');
  return hash;
}
function resetDatabase() {
  db.set('users', [
    {
      username: 'start@start.de',
      password:
        'a7f3d80f39dc2d4c947d5f38bc160050e1660416b5695b61631392b1b2e79aee',
      id: 1,
    },
    {
      username: 'start2@start.de',
      password:
        'a7f3d80f39dc2d4c947d5f38bc160050e1660416b5695b61631392b1b2e79aee',
      id: 2,
    },
  ]).write();
}
//resetDatabase();

// Generiert Valide JTW für Hasura


const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const fs = require('fs');

const privateKey = fs.readFileSync('private.pem');
app.use(bodyParser.json());
app.get('/', function (req, res) {
  res.send('Hallo Auth Service');
});
app.post('/register', async function (req, res) {
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
      'x-hasura-default-role':'user',
      'x-hasura-role': 'user',
      'x-hasura-user-id': answer.uid,
    },
  };

  const token = jwt.sign(tokenData, privateKey, { algorithm: 'RS256' });
  res.send(token);
});
app.post("/get_package", async function (req, res) {
  code = await generatCode(10)
  res.send(code)
})

app.listen(3000);

async function checkIfUserAccountExistsAndPasswordCorrect(username, password) {
  if(password == 'wrongpassword') {
    console.log(password)
    return false
  }
  const uid = '123';
  const sub = '123';
  const name = username;
  const answer = {
    sub,
    name,
    uid,
  };
  return answer;
}
async function generatCode(fachnummer) {
  
  let randomNumber = []
  randomNumber.push(getDigitAtPos(0,fachnummer))
  randomNumber.push(getDigitAtPos(1, fachnummer))
  for (let index = 0; index < 3; index++) {
    const element = Math.floor(Math.random() * 10);;
    randomNumber.push(element)
  }
  return randomNumber
}
function getDigitAtPos(n, number) {
// convert number to a string, then extract the first digit
var one = String(number).charAt(n);

// convert the first digit back to an integer
var one_as_number = Number(one); 
return one_as_number;
}


package com.example.boxbase.data;

import android.os.AsyncTask;

import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        if(username.isEmpty())
                return new Result.Error(new SecurityException("no username"));
        try {
            String token = new AuthentificationTask().execute(username, password).get();
            if(token.isEmpty())
                throw new IOException("Fehlerhafte Login-Daten");
            LoggedInUser user = new LoggedInUser(username, username, token);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private String name;
    private boolean waitingForResponse;

    class AuthentificationTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... pair) {
            String token = "";
                String json = HttpUtilities.getJsonPost(pair[0], pair[1]);
            try {
                token = HttpUtilities.doPostRequest(HttpUtilities.getAuthServiceUrl(), json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return token;
        }
    }
}


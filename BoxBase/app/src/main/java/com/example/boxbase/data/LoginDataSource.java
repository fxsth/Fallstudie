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
            username = username.toLowerCase();
            String token = new AuthentificationTask().execute().get();

            //String test = testGraphQl(token);
            LoggedInUser user = new LoggedInUser(username, name, token);
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
}

class AuthentificationTask extends AsyncTask<Void, Void, String> {
    protected String doInBackground(Void... voids) {
        String json = HttpUtilities.getJsonPost("max@web.de", "test1234");
        String token = "";
        try {
            token = HttpUtilities.doPostRequest(HttpUtilities.getAuthServiceUrl(), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }
}
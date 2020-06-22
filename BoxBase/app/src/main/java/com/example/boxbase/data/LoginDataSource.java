package com.example.boxbase.data;

import android.os.AsyncTask;

import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        if(username.isEmpty())
                return new Result.Error(new SecurityException("no username"));
        try {
            String response = new AuthentificationTask().execute(username, password).get();
            String token = getToken(response);
            int id = getId(response);
            if(token.isEmpty())
                throw new IOException("Fehlerhafte Login-Daten");
            LoggedInUser user = new LoggedInUser(id, username, token);
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

    private String getToken(String jsonString)
    {
        String token ="";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            token = (String) jsonObject.get("jwt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }
    private int getId(String jsonString)
    {
        int id=0;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            id = (int) jsonObject.get("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    class AuthentificationTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... pair) {
            String response = "";
                String json = HttpUtilities.getJsonPost(pair[0], pair[1]);
            try {
                response = HttpUtilities.doPostRequest(HttpUtilities.getAuthServiceUrl(), json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }
}


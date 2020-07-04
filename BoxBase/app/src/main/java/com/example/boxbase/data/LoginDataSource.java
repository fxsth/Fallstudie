package com.example.boxbase.data;

import android.os.AsyncTask;

import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        if(username.isEmpty())
                return new Result.Error(new SecurityException("no username"));
        try {
            return new AuthentificationTask().execute(username, password).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new Result.Error(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Result.Error(e);
        }
    }

    public Result<LoggedInUser> register(String username, String password, String name) {
        if(username.isEmpty() || password.isEmpty() || name.isEmpty())
            return new Result.Error(new SecurityException("Missing Data"));
        try {
            return new RegistrationTask().execute(username, password, name).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new Result.Error(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Result.Error(e);
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private String name;
    private boolean waitingForResponse;

    private String getToken(String jsonString) throws SecurityException
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

    class AuthentificationTask extends AsyncTask<String, Void, Result<LoggedInUser>> {
        protected Result<LoggedInUser> doInBackground(String... pair) {
            String response = "";
                String json = HttpUtilities.getAuthJsonPost(pair[0], pair[1]);
            try {
                response = HttpUtilities.doPostRequest(HttpUtilities.getAuthServiceUrl(), json);
                return new Result.Success<>(new LoggedInUser(getId(response), pair[0], getToken(response)));
            } catch (IOException e) {
                e.printStackTrace();
                return new Result.Error(e);
            }
        }
    }

    class RegistrationTask extends AsyncTask<String, Void, Result<LoggedInUser>> {
        protected Result<LoggedInUser> doInBackground(String... pair) {
            String response = "";
            String json = HttpUtilities.getAuthJsonPost(pair[0], pair[1]);
            try {
                response = HttpUtilities.doPostRequest(HttpUtilities.getRegisterServiceUrl(), json);
                return new Result.Success<>(new LoggedInUser(getId(response), pair[0], pair[2], getToken(response)));
            } catch (IOException e) {
                e.printStackTrace();
                return new Result.Error(e);
            }
        }
    }
}


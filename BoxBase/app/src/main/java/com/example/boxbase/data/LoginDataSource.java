package com.example.boxbase.data;

import android.os.AsyncTask;

import com.example.boxbase.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

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
        Requester requester = new Requester();
        String json = requester.getJsonPost("max@web.de", "test1234");
        String token = "";
        try {
            token = requester.doPostRequest("http://roman.technology:3000/register", json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

}

class Requester {

    private OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    // test data
    String getJsonPost(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }
}

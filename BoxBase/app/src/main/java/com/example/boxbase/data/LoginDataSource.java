package com.example.boxbase.data;

import android.os.AsyncTask;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.*;
import com.example.boxbase.data.model.LoggedInUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.os.SystemClock.sleep;

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

            // TODO: handle loggedInUser authentication
            String test = testGraphQl(token);
            LoggedInUser user = new LoggedInUser(username, name);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }




    private String testGraphQl(String token)
    {
        waitingForResponse = true;

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Authorization", "Bearer "+token);
                    return chain.proceed(builder.build());
                })
                .build();


        ApolloClient apolloClient = ApolloClient.builder().serverUrl("http://roman.technology:8080/v1/graphql").okHttpClient(httpClient).build();
        ApolloQueryCall<PaketeQuery.Data> query = apolloClient.query(new PaketeQuery());
        query.enqueue(new ApolloCall.Callback<PaketeQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<PaketeQuery.Data> response) {
                if(response.getData() !=null)
                {
                    Log.d("GraphQLAntwort", response.getData().toString() );
                    name = response.getData().pakete().toString();
                    waitingForResponse = false;
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d("GraphQlFehler", e.toString() );
                waitingForResponse = false;
            }
        });
        int i =0;
        while(waitingForResponse) {
            sleep(99);
            if(i>100)
            {
                throw new IllegalThreadStateException();
            }
            i++;
        }
        return name;
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

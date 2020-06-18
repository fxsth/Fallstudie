package com.example.boxbase.data;

import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.TestQuery;   // wird von Gradle generiert beim Bauen
import com.example.boxbase.data.model.LoggedInUser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

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
            name = getNameByUsername(username);

            // TODO: handle loggedInUser authentication

            LoggedInUser user = new LoggedInUser(username, name);
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private String getNameByUsername(String email)
    {
        waitingForResponse = true;

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("x-hasura-admin-secret", "boxbaseFI");    // Provisorisch mit Admin-Key: Bei Value muss das Admin-Passwort eingegeben werden
                    return chain.proceed(builder.build());
                })
                .build();


        ApolloClient apolloClient = ApolloClient.builder().serverUrl("http://f.dedyn.io:8080/v1/graphql").okHttpClient(httpClient).build();
        ApolloQueryCall<TestQuery.Data> query = apolloClient.query(new TestQuery(email));
        query.enqueue(new ApolloCall.Callback<TestQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<TestQuery.Data> response) {
                if(response.getData() !=null)
                {
                    Log.d("GraphQLAntwort", response.getData().toString() );
                    name = response.getData().user_by_pk().Name();
                    waitingForResponse = false;
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.d("GraphQlFehler", e.toString() );
                waitingForResponse = false;
            }
        });
        while(waitingForResponse) {
            sleep(99);
        }
        return name;
    }

    private String name;
    private boolean waitingForResponse;
}
package com.example.boxbase.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.UserQuery;
import com.example.boxbase.MainMenuActivity;
import com.example.boxbase.R;
import com.example.boxbase.data.LoginRepository;
import com.example.boxbase.data.Result;
import com.example.boxbase.data.model.LoggedInUser;
import com.example.boxbase.network.HttpUtilities;

import org.jetbrains.annotations.NotNull;

import okhttp3.OkHttpClient;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private Context context;
    private SharedPreferences sp1;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(username, password);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(data.getToken());
            ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).build();
            UserQuery userQuery = UserQuery.builder().userid(data.getUserId()).build();
            apolloClient.query(userQuery).enqueue(new ApolloCall.Callback<UserQuery.Data>() {
              @Override
              public void onResponse(@NotNull Response<UserQuery.Data> response) {
                  if (response.hasErrors()) {
                      Log.d("GraphQL", "Query fehlerhaft");
                      Log.d("GraphQL", response.getErrors().get(0).getMessage());
                  }
                  LoggedInUser newUser = new LoggedInUser(data.getUserId(), data.getDisplayName(), response.getData().person().get(0).name(), data.getToken());
                  loginRepository.setLoggedInUser(newUser);
                  loginResult.postValue(new LoginResult(new LoggedInUserView(response.getData().person().get(0).name())));
              }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                loginResult.postValue(new LoginResult(result.toString()));
            }});

        } else {
            loginResult.setValue(new LoginResult(result.toString()));
        }
    }

    public void register(String username, String password, String name) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.register(username, password, name);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(result.toString()));
        }
    }

    public void loginDataChanged(String username, String password) {
        if(username.isEmpty())
        {
            loginFormState.setValue(new LoginFormState(R.string.empty_username, null));
        } else if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        //} else if (!isPasswordValid(password)) {
        //  loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    public boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public void loadSavedLogInAccess(Context context) {
        Result<LoggedInUser> result = loginRepository.loadSavedLogInAccess(context);
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            OkHttpClient httpClient = HttpUtilities.getHttpAuthorizationClient(data.getToken());
            ApolloClient apolloClient = ApolloClient.builder().serverUrl(HttpUtilities.getGraphQLUrl()).okHttpClient(httpClient).build();
            UserQuery userQuery = UserQuery.builder().userid(data.getUserId()).build();
            apolloClient.query(userQuery).enqueue(new ApolloCall.Callback<UserQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<UserQuery.Data> response) {
                    if (response.hasErrors()) {
                        Log.d("GraphQL", "Query fehlerhaft");
                        Log.d("GraphQL", response.getErrors().get(0).getMessage());
                    }

                    loginResult.postValue(new LoginResult(new LoggedInUserView(response.getData().person().get(0).name())));
                    LoggedInUser newUser = new LoggedInUser(data.getUserId(), data.getDisplayName(), response.getData().person().get(0).name(), data.getToken());
                    loginRepository.setLoggedInUser(newUser);
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    loginResult.postValue(new LoginResult(result.toString()));
                }});
        }
    }
}
package com.example.boxbase.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.boxbase.data.model.LoggedInUser;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;
    private SharedPreferences sp1;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
        SharedPreferences.Editor editor = sp1.edit();
        editor.clear();
        editor.commit();
    }

    public void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> login(String username, String password) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            saveLogInAccess(username, password);
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public Result<LoggedInUser> register(String username, String password, String name) {
        Result<LoggedInUser> result = dataSource.register(username, password, name);
        if (result instanceof Result.Success) {
            saveLogInAccess(username, password);
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public LoggedInUser getUser() {
        return user;
    }

    public Result<LoggedInUser> loadSavedLogInAccess(Context context) {
        String username = "";
        String password = "";
        sp1 = context.getSharedPreferences("Login", MODE_PRIVATE);
        if (sp1.contains("Unm") && sp1.contains("Psw")) {
            username = sp1.getString("Unm", null);
            password = sp1.getString("Psw", null);
            if (!username.isEmpty() && !password.isEmpty()) {
                return login(username, password);
            }
        }
        return new Result.Nothing();
    }

    public void saveLogInAccess(String username, String password) {
        if (sp1 != null) {
            SharedPreferences.Editor Ed = sp1.edit();
            Ed.putString("Unm", username);
            Ed.putString("Psw", password);
            Ed.commit();
        }
    }
}
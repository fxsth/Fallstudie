package com.example.boxbase.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private int userId;
    private String displayName;
    private String token;
    private String email = "";

    public LoggedInUser(int userId, String displayName, String token) {
        this.userId = userId;
        this.displayName = displayName;
        this.token = token;
    }
    public LoggedInUser(int userId, String email, String displayName, String token) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getToken(){return token;}

    public String getEmail(){return email;}
}

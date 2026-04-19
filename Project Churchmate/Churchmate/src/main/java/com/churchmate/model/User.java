package com.churchmate.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;

    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getProfile() { return username + " (" + role + ")"; }
    public void setPassword(String password) { this.password = password; }
}
package com.example.goosenetmobile.classes;

public class User {
    private String UserName;
    private String FullName;
    private String Role;
    private String Email;
    private String Password;
    private String ProfilePicString;
    private String DefaultPicString;
    private String ApiKey;

    public User() {
        // No-arg constructor required for Firebase
    }

    public String getUserName() { return UserName; }
    public void setUserName(String userName) { this.UserName = userName; }

    public String getFullName() { return FullName; }
    public void setFullName(String fullName) { this.FullName = fullName; }

    public String getRole() { return Role; }
    public void setRole(String role) { this.Role = role; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { this.Email = email; }

    public String getPassword() { return Password; }
    public void setPassword(String password) { this.Password = password; }

    public String getProfilePicString() { return ProfilePicString; }
    public void setProfilePicString(String profilePicString) { this.ProfilePicString = profilePicString; }

    public String getDefaultPicString() { return DefaultPicString; }
    public void setDefaultPicString(String defaultPicString) { this.DefaultPicString = defaultPicString; }

    public String getApiKey() { return ApiKey; }
    public void setApiKey(String apiKey) { this.ApiKey = apiKey; }
}
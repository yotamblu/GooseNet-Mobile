package com.example.goosenetmobile.classes;

public class UserAuthData {
    private String userName;
    private String hashedPassword;

    public UserAuthData(String UserName, String HashedPassword) {
        userName = UserName;
        hashedPassword = HashedPassword;
    }
    public UserAuthData(){

    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        userName = userName;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        hashedPassword = hashedPassword;
    }
}

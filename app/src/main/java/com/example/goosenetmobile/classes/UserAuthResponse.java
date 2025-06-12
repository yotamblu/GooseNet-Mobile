package com.example.goosenetmobile.classes;

import com.google.gson.annotations.SerializedName;

public class UserAuthResponse {
    @SerializedName("message")
    public String Message;
    @SerializedName("authorized")
    public boolean IsAuthorized;
    @SerializedName("apiKey")
    public String ApiKey;

    public UserAuthResponse(){
        //parameterless constructor for deserialization with GSON
    }

    public UserAuthResponse(String message, boolean isAuthorized) {
        Message = message;
        IsAuthorized = isAuthorized;
    }

    public UserAuthResponse(String message , boolean isAuthorized,String apiKey){
        Message = message;
        IsAuthorized = isAuthorized;
        ApiKey = apiKey;
    }

    public String getApiKey() {
        return ApiKey;
    }

    public void setApiKey(String apiKey) {
        ApiKey = apiKey;
    }


    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isAuthorized() {
        return IsAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        IsAuthorized = authorized;
    }
}
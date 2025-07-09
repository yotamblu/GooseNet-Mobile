package com.example.goosenetmobile.classes;

import com.google.gson.annotations.SerializedName;

public class RequestTokenResponse {
    @SerializedName("oauth_token")
    public String OAuthToken;
    @SerializedName("oauth_token_secret")
    public String OAuthTokenSecret;

    public RequestTokenResponse(){
        //for GSON
    }

}

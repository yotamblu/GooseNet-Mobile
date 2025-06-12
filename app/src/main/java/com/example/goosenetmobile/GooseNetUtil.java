package com.example.goosenetmobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.security.MessageDigest;

public class GooseNetUtil {
     static final String IS_LOGGEDIN_KEY  = "loggedInUserName";
    public static void LogUserIn(String userName, String apiKey, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString("IS_LOGGED_IN_USER", userName)
                .putString("apiKey", apiKey)
                .apply();
    }

    public static boolean isLoggedIn(Activity activity){
        return !activity.getPreferences(Context.MODE_PRIVATE).getString(IS_LOGGEDIN_KEY,"").equals("");
    }


    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error computing SHA-256 hash", e);
        }
    }
}

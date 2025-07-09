package com.example.goosenetmobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.security.MessageDigest;

public class GooseNetUtil {
     static final String IS_LOGGEDIN_KEY  = "loggedInUserName";
    public static void LogUserIn(String userName, String apiKey, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(IS_LOGGEDIN_KEY, userName)
                .putString("apiKey", apiKey)
                .apply();
    }
    public static String getApiKey(Context context) {
       return PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey","");
    }


    public static boolean isLoggedIn(Context context){
        return !PreferenceManager.getDefaultSharedPreferences(context).getString("loggedInUserName","").equals("");
    }
    public static Bitmap base64ToBitmap(String base64StringWithPrefix) {
        // Remove the data:image/...;base64, part if it exists
        if (base64StringWithPrefix.contains(",")) {
            base64StringWithPrefix = base64StringWithPrefix.substring(base64StringWithPrefix.indexOf(",") + 1);
        }

        byte[] decodedBytes = Base64.decode(base64StringWithPrefix, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
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

package com.example.goosenetmobile;

import android.util.Log;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class HttpsHelper {

    public interface HttpCallback {
        void onSuccess(String response);
        void onError(Exception ex);
    }

    public static void sendGet(String urlStr, HttpCallback callback) {
        new Thread(() -> {
            HttpsURLConnection conn = null;
            try {
                URL url = new URL(urlStr);
                conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                int responseCode = conn.getResponseCode();
                InputStream stream = (responseCode >= 400)
                        ? conn.getErrorStream()
                        : conn.getInputStream();

                if (stream == null) {
                    callback.onError(new IOException("Empty response stream for code " + responseCode));
                    return;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(stream));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                callback.onSuccess(response.toString());
            } catch (Exception e) {
                Log.e("HttpsHelper", "GET failed: " + urlStr, e);
                callback.onError(e);
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    public static void sendPost(String urlStr, String postData, HttpCallback callback) {
        HttpsURLConnection con = null;
        try {
            URL url = new URL(urlStr);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            InputStream stream = (responseCode >= 400)
                    ? con.getErrorStream()
                    : con.getInputStream();

            if (stream == null) {
                callback.onError(new IOException("Empty response stream for code " + responseCode));
                return;
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                callback.onSuccess(response.toString());
            }
        } catch (Exception e) {
            Log.e("HttpsHelper", "POST failed: " + urlStr, e);
            callback.onError(e);
        } finally {
            if (con != null) con.disconnect();
        }
    }
}

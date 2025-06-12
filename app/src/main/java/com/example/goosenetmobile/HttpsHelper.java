package com.example.goosenetmobile;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;
public class HttpsHelper {


    public interface HttpCallback {
        void onSuccess(String response);
        void onError(Exception ex);
    }


    public static void sendGet(String urlStr, HttpCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                callback.onSuccess(response.toString());
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void sendPost(String urlStr, String postData,  HttpCallback callback) {


        try {
            URL url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code: " + responseCode);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                callback.onSuccess(response.toString());
                System.out.println("POST Response Code: " + response.toString());

            }

        } catch (Exception e) {
            callback.onError(e);
        }
    }

}

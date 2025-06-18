package com.example.goosenetmobile;
import android.util.Log;
import android.speech.RecognitionService;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.goosenetmobile.classes.BasicMessage;
import com.example.goosenetmobile.classes.GetRoleResponse;
import com.example.goosenetmobile.classes.User;
import com.example.goosenetmobile.classes.UserAuthData;
import com.example.goosenetmobile.classes.UserAuthResponse;
import com.google.gson.Gson;

import java.util.concurrent.CountDownLatch;

public class ApiService {



    final static  String GOOSEAPI_BASE_URL = "https://gooseapi.bsite.net/api";




    public static boolean RegisterUser(User userData)  {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};
        String jsonData = new Gson().toJson(userData);
        System.out.println(jsonData);
        HttpsHelper.sendPost(GOOSEAPI_BASE_URL + "/registration", jsonData,new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                result[0] =
                new Gson().fromJson(response, BasicMessage.class).getMessage().equals("User Registered Successfully");
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                latch.countDown();
            }


        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result[0];
    }




    public static boolean AuthUser(String userName,String password) throws  Exception{
        final boolean[] result = {false};
        CountDownLatch latch = new CountDownLatch(1);

        HttpsHelper.sendPost(GOOSEAPI_BASE_URL + "/userAuth",

                new Gson().toJson(new UserAuthData(userName, GooseNetUtil.sha256(password))), new HttpsHelper.HttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        System.out.println(response);
                        result[0] = new Gson().fromJson(response, UserAuthResponse.class).isAuthorized();
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception ex) {
                        result[0] = false;
                        latch.countDown();
                    }
                });

        latch.await();
        System.out.println(result[0]);
        return result[0];
    }


    public static String getRole (String apiKey){
        CountDownLatch latch = new CountDownLatch(1);
        final String[] result = {""};
        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/getRole?apiKey=" + apiKey, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {

                result[0] = new Gson().fromJson(response, GetRoleResponse.class).role;
                System.out.println(response);
                latch.countDown();

            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR" + ex.getMessage());
                latch.countDown();
            }
        });


        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result[0];

    }


    public static String getApiKey(String userName,String password) throws  Exception{
        final String[] result = {""};
        CountDownLatch latch = new CountDownLatch(1);

        HttpsHelper.sendPost(GOOSEAPI_BASE_URL + "/userAuth",

                new Gson().toJson(new UserAuthData(userName, password)), new HttpsHelper.HttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        System.out.println(response);
                        result[0] = new Gson().fromJson(response, UserAuthResponse.class).getApiKey();
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception ex) {
                        ex.printStackTrace();
                        System.out.println("ERR:" + ex.getMessage());
                        latch.countDown();
                    }
                });

        latch.await();
        System.out.println(result[0]);
        return result[0];
    }

}

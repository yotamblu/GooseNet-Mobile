package com.example.goosenetmobile;
import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.Log;
import android.speech.RecognitionService;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.goosenetmobile.classes.ActivityData;
import com.example.goosenetmobile.classes.AddToFlockData;
import com.example.goosenetmobile.classes.AthleteCard;
import com.example.goosenetmobile.classes.BasicMessage;
import com.example.goosenetmobile.classes.GetAthletesResponseData;
import com.example.goosenetmobile.classes.GetRoleResponse;
import com.example.goosenetmobile.classes.IsConnectedResponse;
import com.example.goosenetmobile.classes.PlannedWorkout;
import com.example.goosenetmobile.classes.PlannedWorkoutData;
import com.example.goosenetmobile.classes.PlannedWorkoutResponse;
import com.example.goosenetmobile.classes.RequestTokenResponse;
import com.example.goosenetmobile.classes.User;
import com.example.goosenetmobile.classes.UserAuthData;
import com.example.goosenetmobile.classes.UserAuthResponse;
import com.example.goosenetmobile.classes.Workout;
import com.example.goosenetmobile.classes.WorkoutExtensiveData;
import com.example.goosenetmobile.classes.WorkoutSummary;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.PhantomReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class ApiService {



    final static  String GOOSEAPI_BASE_URL = "https://gooseapi.ddns.net/api";


    public static boolean changeProfilePic(String base64String, boolean isRevert , String apiKey){
        String requestUrl = GOOSEAPI_BASE_URL + "/editProfile/changePic?apiKey=" + apiKey + "&isRevert=" + String.valueOf(isRevert);
        String data = "{\"PicString\":\"" + base64String +"\"}";
        boolean[] result = {false};
        CountDownLatch latch = new CountDownLatch(1);
        HttpsHelper.sendPost(requestUrl, data, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                result[0] = JsonParser
                        .parseString(response).
                        getAsJsonObject().
                        get("message").getAsString()
                        .equals("Profile Picture Updated Successfully");
                latch.countDown();

            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return  result[0];
    }

    public static boolean changePassword(String newPassword,String apiKey){
         String requestUrl = GOOSEAPI_BASE_URL + "/editProfile/changePassword?apiKey=" + apiKey;
         boolean[] result =  {false};
         CountDownLatch latch = new CountDownLatch(1);
         String data =  "{" +
                 "\"NewPassword\":\"" + newPassword + "\"" +
                 "}";
         System.out.println(data);
         HttpsHelper.sendPost(requestUrl, data, new HttpsHelper.HttpCallback() {
             @Override
             public void onSuccess(String response) {
                 result[0] = JsonParser
                         .parseString(response).
                         getAsJsonObject().
                         get("message").getAsString()
                         .equals("password changed successfully");
                 latch.countDown();
             }

             @Override
             public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
                 latch.countDown();
             }
         });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return  result[0];
    }


    public static PlannedWorkoutResponse getPlannedWorkoutById(String workoutId){
        final PlannedWorkoutResponse[] result = {null};
        String requestUrl = GOOSEAPI_BASE_URL + "/plannedWorkout/byId?id=" + workoutId;
        CountDownLatch latch = new CountDownLatch(1);
        HttpsHelper.sendGet(requestUrl, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                if(!JsonParser.parseString(response).getAsJsonObject().has("message")){
                    result[0] = new Gson().fromJson(response,PlannedWorkoutResponse.class);
                }
                Log.i("API SERVICE", response);
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                Log.e("API SERVICE","ERR:" + ex.getMessage());
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

    //NO LEADING ZEROS FOR THE DATE!!!!
    public static List<PlannedWorkout> getPlannedWorkoutsByDate(String athleteName, String date,Context context){
        final List[] result = {null};
        String apiKey = PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey","");
        CountDownLatch latch = new CountDownLatch(1);
        String requestUrl = GOOSEAPI_BASE_URL + "/plannedWorkout/byDate?athleteName=" + athleteName + "&apiKey=" + apiKey + "&date=" + date;
        System.out.println(requestUrl);
        HttpsHelper.sendGet(requestUrl, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

                JsonElement workoutsElement = jsonObject.get("runningWorkouts");

                Type listType = new TypeToken<List<PlannedWorkout>>() {}.getType();
                result[0] = gson.fromJson(workoutsElement, listType);

                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                Log.e("API SERVICE","ERR:" + ex.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return  result[0];
    }





    public static ActivityData getWorkoutById(String athleteName, String workoutId){
        final ActivityData[] result = {null};
        CountDownLatch latch = new CountDownLatch(1);

        String requestUrl = GOOSEAPI_BASE_URL + "/workoutSummary/getWorkout?userName=" + athleteName + "&id=" + workoutId;

        HttpsHelper.sendGet(requestUrl, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                if(!JsonParser.parseString(response).getAsJsonObject().has("message")){
                    result[0] = new Gson().fromJson(response,ActivityData.class);
                }
                Log.i("API SERVICE",response);
                latch.countDown();

            }

            @Override
            public void onError(Exception ex) {
                Log.e("API service error","ERR:" + ex.getMessage());
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


    public static WorkoutExtensiveData getWorkoutExtensiveData(String athleteName,String workoutId){
        final WorkoutExtensiveData[] result = {null};

        CountDownLatch latch = new CountDownLatch(1);
        String requestUrl = GOOSEAPI_BASE_URL + "/workoutSummary/data?userName=" + athleteName + "&workoutId=" + workoutId;
        Log.i("API SERVICE",requestUrl);
        HttpsHelper.sendGet(requestUrl, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
               if(!JsonParser.parseString(response).getAsJsonObject().has("message")){
                 result[0] =   new Gson().fromJson(response,WorkoutExtensiveData.class);

               }
               System.out.println(response);
               latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
                latch.countDown();
            }
        });

        try{
            latch.await();
        }catch (Exception ex){

        }
        result[0].getWorkoutLaps().get(result[0].getWorkoutLaps().size() - 1).lapDistanceInKilometers /= 100f;


        return  result[0];
    }




    public static List<WorkoutSummary> getWorkoutSummaries(Context context, String date,String athleteName ){
        String apiKey = PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey","");
        CountDownLatch latch = new CountDownLatch(1);
        final List<WorkoutSummary>[] workoutList = new ArrayList[1];
        String requestUrl = GOOSEAPI_BASE_URL + "/workoutSummary?apiKey=" + apiKey + "&athleteName=" + athleteName + "&date=" + date;
        HttpsHelper.sendGet(requestUrl, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

                JsonElement workoutsElement = jsonObject.get("runningWorkouts");

                Type listType = new TypeToken<List<WorkoutSummary>>() {}.getType();
                workoutList[0] = gson.fromJson(workoutsElement, listType);

                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return workoutList[0];
    }


    public static boolean addToFlock(String athleteName,String flockName,Context context){
        String apiKey = PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey","");
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};
        AddToFlockData data = new AddToFlockData(athleteName,flockName);
        String requestUrl = GOOSEAPI_BASE_URL + "/flocks/addToFlock?apiKey=" + apiKey;
        HttpsHelper.sendPost(requestUrl, new Gson().toJson(data), new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                result[0] = JsonParser.parseString(response).getAsJsonObject().get("message").getAsString()
                        .equals("athlete added to flock successfully");
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return  result[0];
    }

    @NonNull
    public static List<String> getPotentialFlocks(String athleteName , Context context){
        List<String> flockNames = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        String requestUrl = GOOSEAPI_BASE_URL + "/flocks/getPotentialFlocks?apiKey=" +
                PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey","") +
                "&athleteName=" + athleteName;
        HttpsHelper.sendGet(requestUrl, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    if (jsonResponse.has("potentialFlocks")){
                        JSONArray flocksArray = jsonResponse.getJSONArray("potentialFlocks");
                        for (int i = flocksArray.length() - 1; i >= 0; i--) {
                            String flockName = flocksArray.getString(i);
                            flockNames.add(0, flockName);  // Insert at top
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Add each flock name to the top of the result list
               latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return flockNames;
    }

    public static boolean createFlock(String flockName,Context context){
        final boolean[] result = {false};
        CountDownLatch latch = new CountDownLatch(1);
        String createFlockUrl = GOOSEAPI_BASE_URL + "/flocks/createFlock?apiKey=" +
                PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey","")+ "&flockName=" +
                flockName;
        HttpsHelper.sendPost(createFlockUrl, "", new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                if(JsonParser.parseString(response).getAsJsonObject().get("message").getAsString().equals("Flock created successfully")){
                    result[0] = true;
                }
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
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

    public static List<String> getFlockNames(Context context) {
        CountDownLatch latch = new CountDownLatch(1);
        final ArrayList<String> result = new ArrayList<>();

        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/flocks/getFlocks?apiKey=" +
                PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey", ""), new HttpsHelper.HttpCallback() {

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray flocksArray = jsonResponse.getJSONArray("flocks");

                    // Add each flock name to the top of the result list
                    for (int i = flocksArray.length() - 1; i >= 0; i--) {
                        String flockName = flocksArray.getString(i);
                        result.add(0, flockName);  // Insert at top
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }




    public static boolean addWorkout(String jsonBody,String workoutDate,String targetName,boolean isFlock,Context context)  {
        PlannedWorkoutData workoutData = new PlannedWorkoutData(
                targetName,
                isFlock,
                jsonBody,
                workoutDate
        );
        System.out.println(new Gson().toJson(workoutData));
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};

        HttpsHelper.sendPost(
                GOOSEAPI_BASE_URL + "/addWorkout?apiKey=" + PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey", ""),
                new Gson().toJson(workoutData),
                new HttpsHelper.HttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        if(JsonParser.parseString(response).getAsJsonObject().has("meessage")){
                            result[0] = true;
                        }else{
                            result[0] = false;
                        }
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.out.println("ERR:" + ex.getMessage());
                        latch.countDown();
                    }
                }

        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result[0];
    }


    public static List<AthleteCard> getAthletesData(Context context){
        String apiKey = PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey","");
        List<AthleteCard>[] athleteList = new List[1];
        CountDownLatch latch = new CountDownLatch(1);
        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/athletes?apiKey=" + apiKey, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                athleteList[0] = new Gson().fromJson(response, GetAthletesResponseData.class).athletesData;
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
                latch.countDown();

            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return athleteList[0];
    }



    public static boolean connectToCoach(String coachId, Context context){
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};
        JSONObject data  =new JSONObject();
        try {
            data.put("coachId",coachId);
            data.put("apiKey", PreferenceManager.getDefaultSharedPreferences(context).getString("apiKey",""));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        HttpsHelper.sendPost(GOOSEAPI_BASE_URL + "/coachConnection/connect", data.toString(), new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                JsonObject respObj = JsonParser.parseString(response).getAsJsonObject();
                if(respObj.has("message")){
                    result[0] = false;
                }else{
                    result[0] = true;
                }
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
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

    public static String getCoachName(String coachId){
        final String[] result = {""};
        CountDownLatch latch = new CountDownLatch(1);
        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/coachConnection/getCoachName?coachId=" + coachId
                , new HttpsHelper.HttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        JsonObject responseJSON = JsonParser.parseString(response)
                                .getAsJsonObject();
                        if(responseJSON.has("coachUsername")){
                            result[0] = responseJSON.get("coachUsername").getAsString();
                        }

                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.out.println("ERR:" + ex.getMessage());
                        latch.countDown();

                    }
                }
        );
        try{
            latch.await();
        }
        catch (InterruptedException e){

        }
        return result[0];
    }


    public static String getCoachId(String coachUserName){
        final String[] result = {""};
        CountDownLatch latch = new CountDownLatch(1);
        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/coachConnection/getCoachId?coachName=" + coachUserName, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                result[0] = JsonParser.parseString(response)
                        .getAsJsonObject().
                        get("coachId").getAsString();
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return  result[0];


    }



    public static boolean connectToGarminAccount(String params){
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};
        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/access-token?" + params, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                result[0] = true;
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
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



    public static String[] getOAuthTokenAndSecret(){
        final String[] oAuthTokenAndSecret = {"oAuthTokenPlaceholder","oAuthSecretPlaceholder"};
        CountDownLatch latch = new CountDownLatch(1);
        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/request-token", new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                RequestTokenResponse requestTokenResponse = new Gson().fromJson(response, RequestTokenResponse.class);
                oAuthTokenAndSecret[0] = requestTokenResponse.OAuthToken;
                oAuthTokenAndSecret[1] = requestTokenResponse.OAuthTokenSecret;
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:"+ex.getMessage());
                latch.countDown();
            }

        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return oAuthTokenAndSecret;
    }

    public static boolean isConnectedToGarmin(String apiKey){
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] result = {false};
        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/ValidateGarminConnection?apiKey=" + apiKey, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {

               result[0] = new Gson().fromJson(response, IsConnectedResponse.class).isConnected;
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
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


    public static Bitmap getProfilePicBitmap(String userName){
        System.out.println("UserName:"+userName);
        final Bitmap[] result = {null};
        CountDownLatch latch = new CountDownLatch(1);
        HttpsHelper.sendGet(GOOSEAPI_BASE_URL + "/profilePic?userName=" + userName, new HttpsHelper.HttpCallback() {
            @Override
            public void onSuccess(String response) {
                result[0] = GooseNetUtil.base64ToBitmap(response);
                System.out.println(response);
                latch.countDown();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("ERR:" + ex.getMessage());
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

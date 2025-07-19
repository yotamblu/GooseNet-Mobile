package com.example.goosenetmobile.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActivityData
{
    @SerializedName("workoutId")
    private long WorkoutId;

    @SerializedName("wokroutName")
    private String WokroutName;

    @SerializedName("workoutDurationInSeconds")
    private int WorkoutDurationInSeconds;

    @SerializedName("workoutDistanceInMeters")
    private float WorkoutDistanceInMeters;

    @SerializedName("workoutAvgHR")
    private int WorkoutAvgHR;

    @SerializedName("workoutAvgPaceInMinKm")
    private float WorkoutAvgPaceInMinKm;

    @SerializedName("workoutLaps")
    private List<FinalLap> WorkoutLaps;

    @SerializedName("workoutCoordsJsonStr")
    private String WorkoutCoordsJsonStr;

    @SerializedName("workoutMapCenterJsonStr")
    private String WorkoutMapCenterJsonStr;

    @SerializedName("workoutMapZoom")
    private double WorkoutMapZoom;

    @SerializedName("workoutDeviceName")
    private String WorkoutDeviceName;

    @SerializedName("userAccessToken")
    private String UserAccessToken;

    @SerializedName("dataSamples")
    private List<DataSample> DataSamples;

    @SerializedName("workoutDate")
    private String WorkoutDate;
    
    
    
    public ActivityData(){
        
    }

    public ActivityData(long workoutId, String wokroutName, int workoutDurationInSeconds, float workoutDistanceInMeters, int workoutAvgHR, float workoutAvgPaceInMinKm, List<FinalLap> workoutLaps, String workoutCoordsJsonStr, String workoutMapCenterJsonStr, double workoutMapZoom, String workoutDeviceName, String userAccessToken, List<DataSample> dataSamples, String workoutDate) {
        WorkoutId = workoutId;
        WokroutName = wokroutName;
        WorkoutDurationInSeconds = workoutDurationInSeconds;
        WorkoutDistanceInMeters = workoutDistanceInMeters;
        WorkoutAvgHR = workoutAvgHR;
        WorkoutAvgPaceInMinKm = workoutAvgPaceInMinKm;
        WorkoutLaps = workoutLaps;
        WorkoutCoordsJsonStr = workoutCoordsJsonStr;
        WorkoutMapCenterJsonStr = workoutMapCenterJsonStr;
        WorkoutMapZoom = workoutMapZoom;
        WorkoutDeviceName = workoutDeviceName;
        UserAccessToken = userAccessToken;
        DataSamples = dataSamples;
        WorkoutDate = workoutDate;
    }

    public long getWorkoutId() {
        return WorkoutId;
    }

    public void setWorkoutId(long workoutId) {
        WorkoutId = workoutId;
    }

    public String getWokroutName() {
        return WokroutName;
    }

    public void setWokroutName(String wokroutName) {
        WokroutName = wokroutName;
    }

    public int getWorkoutDurationInSeconds() {
        return WorkoutDurationInSeconds;
    }

    public void setWorkoutDurationInSeconds(int workoutDurationInSeconds) {
        WorkoutDurationInSeconds = workoutDurationInSeconds;
    }

    public float getWorkoutDistanceInMeters() {
        return WorkoutDistanceInMeters;
    }

    public void setWorkoutDistanceInMeters(float workoutDistanceInMeters) {
        WorkoutDistanceInMeters = workoutDistanceInMeters;
    }

    public int getWorkoutAvgHR() {
        return WorkoutAvgHR;
    }

    public void setWorkoutAvgHR(int workoutAvgHR) {
        WorkoutAvgHR = workoutAvgHR;
    }

    public float getWorkoutAvgPaceInMinKm() {
        return WorkoutAvgPaceInMinKm;
    }

    public void setWorkoutAvgPaceInMinKm(float workoutAvgPaceInMinKm) {
        WorkoutAvgPaceInMinKm = workoutAvgPaceInMinKm;
    }

    public List<FinalLap> getWorkoutLaps() {
        return WorkoutLaps;
    }

    public void setWorkoutLaps(List<FinalLap> workoutLaps) {
        WorkoutLaps = workoutLaps;
    }

    public String getWorkoutCoordsJsonStr() {
        return WorkoutCoordsJsonStr;
    }

    public void setWorkoutCoordsJsonStr(String workoutCoordsJsonStr) {
        WorkoutCoordsJsonStr = workoutCoordsJsonStr;
    }

    public String getWorkoutMapCenterJsonStr() {
        return WorkoutMapCenterJsonStr;
    }

    public void setWorkoutMapCenterJsonStr(String workoutMapCenterJsonStr) {
        WorkoutMapCenterJsonStr = workoutMapCenterJsonStr;
    }

    public double getWorkoutMapZoom() {
        return WorkoutMapZoom;
    }

    public void setWorkoutMapZoom(double workoutMapZoom) {
        WorkoutMapZoom = workoutMapZoom;
    }

    public String getWorkoutDeviceName() {
        return WorkoutDeviceName;
    }

    public void setWorkoutDeviceName(String workoutDeviceName) {
        WorkoutDeviceName = workoutDeviceName;
    }

    public String getUserAccessToken() {
        return UserAccessToken;
    }

    public void setUserAccessToken(String userAccessToken) {
        UserAccessToken = userAccessToken;
    }

    public List<DataSample> getDataSamples() {
        return DataSamples;
    }

    public void setDataSamples(List<DataSample> dataSamples) {
        DataSamples = dataSamples;
    }

    public String getWorkoutDate() {
        return WorkoutDate;
    }

    public void setWorkoutDate(String workoutDate) {
        WorkoutDate = workoutDate;
    }
}

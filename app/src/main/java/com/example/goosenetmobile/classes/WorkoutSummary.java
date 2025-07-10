package com.example.goosenetmobile.classes;

public class WorkoutSummary {
    private String workoutName;
    private long workoutId;

    private int workoutDurationInSeconds;
    private double workoutDistanceInMeters;
    private int workoutAvgHR;

    private double workoutAvgPaceInMinKm;
    private String workoutCoordsJsonStr;

    private String workoutDate;
    private String profilePicData;
    private String athleteName;

    // Full constructor
    public WorkoutSummary(String workoutName, long workoutId,
                          int workoutDurationInSeconds, double workoutDistanceInMeters,
                          int workoutAvgHR, double workoutAvgPaceInMinKm,
                          String workoutCoordsJsonStr, String workoutDate,
                          String profilePicData, String athleteName) {
        this.workoutName = workoutName;
        this.workoutId = workoutId;
        this.workoutDurationInSeconds = workoutDurationInSeconds;
        this.workoutDistanceInMeters = workoutDistanceInMeters;
        this.workoutAvgHR = workoutAvgHR;
        this.workoutAvgPaceInMinKm = workoutAvgPaceInMinKm;
        this.workoutCoordsJsonStr = workoutCoordsJsonStr;
        this.workoutDate = workoutDate;
        this.profilePicData = profilePicData;
        this.athleteName = athleteName;
    }

    // Getters
    public String getWorkoutName() {
        return workoutName;
    }

    public long getWorkoutId() {
        return workoutId;
    }

    public int getWorkoutDurationInSeconds() {
        return workoutDurationInSeconds;
    }

    public double getWorkoutDistanceInMeters() {
        return workoutDistanceInMeters;
    }

    public int getWorkoutAvgHR() {
        return workoutAvgHR;
    }

    public double getWorkoutAvgPaceInMinKm() {
        return workoutAvgPaceInMinKm;
    }

    public String getWorkoutCoordsJsonStr() {
        return workoutCoordsJsonStr;
    }

    public String getWorkoutDate() {
        return workoutDate;
    }

    public String getProfilePicData() {
        return profilePicData;
    }

    public String getAthleteName() {
        return athleteName;
    }

    // Setters
    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public void setWorkoutId(long workoutId) {
        this.workoutId = workoutId;
    }

    public void setWorkoutDurationInSeconds(int workoutDurationInSeconds) {
        this.workoutDurationInSeconds = workoutDurationInSeconds;
    }

    public void setWorkoutDistanceInMeters(double workoutDistanceInMeters) {
        this.workoutDistanceInMeters = workoutDistanceInMeters;
    }

    public void setWorkoutAvgHR(int workoutAvgHR) {
        this.workoutAvgHR = workoutAvgHR;
    }

    public void setWorkoutAvgPaceInMinKm(double workoutAvgPaceInMinKm) {
        this.workoutAvgPaceInMinKm = workoutAvgPaceInMinKm;
    }

    public void setWorkoutCoordsJsonStr(String workoutCoordsJsonStr) {
        this.workoutCoordsJsonStr = workoutCoordsJsonStr;
    }

    public void setWorkoutDate(String workoutDate) {
        this.workoutDate = workoutDate;
    }

    public void setProfilePicData(String profilePicData) {
        this.profilePicData = profilePicData;
    }

    public void setAthleteName(String athleteName) {
        this.athleteName = athleteName;
    }
}
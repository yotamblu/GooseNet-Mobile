package com.example.goosenetmobile.classes;


public class DataSample {

    private int timerDurationInSeconds;
    private int heartRate;
    private double speedMetersPerSecond;
    private double elevationInMeters;

    public DataSample() {
    }

    public DataSample(int timerDurationInSeconds, int heartRate, double speedMetersPerSecond, double elevationInMeters) {
        this.timerDurationInSeconds = timerDurationInSeconds;
        this.heartRate = heartRate;
        this.speedMetersPerSecond = speedMetersPerSecond;
        this.elevationInMeters = elevationInMeters;
    }

    public int getTimerDurationInSeconds() {
        return timerDurationInSeconds;
    }

    public void setTimerDurationInSeconds(int timerDurationInSeconds) {
        this.timerDurationInSeconds = timerDurationInSeconds;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public double getSpeedMetersPerSecond() {
        return speedMetersPerSecond;
    }

    public void setSpeedMetersPerSecond(double speedMetersPerSecond) {
        this.speedMetersPerSecond = speedMetersPerSecond;
    }

    public double getElevationInMeters() {
        return elevationInMeters;
    }

    public void setElevationInMeters(double elevationInMeters) {
        this.elevationInMeters = elevationInMeters;
    }
}

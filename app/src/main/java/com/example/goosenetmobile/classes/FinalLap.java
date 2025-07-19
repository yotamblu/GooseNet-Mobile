package com.example.goosenetmobile.classes;

public class FinalLap {
    public float lapDistanceInKilometers;
    public int lapDurationInSeconds;
    public float lapPaceInMinKm;
    public int avgHeartRate;

    public FinalLap(float distance, int duration, float pace, int hr) {
        this.lapDistanceInKilometers = distance;
        this.lapDurationInSeconds = duration;
        this.lapPaceInMinKm = pace;
        this.avgHeartRate = hr;
    }

    public FinalLap() {
    }

    public float getLapDistanceInKilometers() {
        return lapDistanceInKilometers;
    }

    public void setLapDistanceInKilometers(float lapDistanceInKilometers) {
        this.lapDistanceInKilometers = lapDistanceInKilometers;
    }

    public int getLapDurationInSeconds() {
        return lapDurationInSeconds;
    }

    public void setLapDurationInSeconds(int lapDurationInSeconds) {
        this.lapDurationInSeconds = lapDurationInSeconds;
    }

    public float getLapPaceInMinKm() {
        return lapPaceInMinKm;
    }

    public void setLapPaceInMinKm(float lapPaceInMinKm) {
        this.lapPaceInMinKm = lapPaceInMinKm;
    }

    public int getAvgHeartRate() {
        return avgHeartRate;
    }

    public void setAvgHeartRate(int avgHeartRate) {
        this.avgHeartRate = avgHeartRate;
    }
}

package com.example.goosenetmobile.classes;

import com.example.goosenetmobile.classes.FinalLap;

import java.util.List;

public class WorkoutExtensiveData {

    private List<FinalLap> workoutLaps;
    private List<DataSample> dataSamples;

    public WorkoutExtensiveData() {
    }

    public WorkoutExtensiveData(List<FinalLap> workoutLaps, List<DataSample> dataSamples) {
        this.workoutLaps = workoutLaps;
        this.dataSamples = dataSamples;
    }

    public List<FinalLap> getWorkoutLaps() {
        return workoutLaps;
    }

    public void setWorkoutLaps(List<FinalLap> workoutLaps) {
        this.workoutLaps = workoutLaps;
    }

    public List<DataSample> getDataSamples() {
        return dataSamples;
    }

    public void setDataSamples(List<DataSample> dataSamples) {
        this.dataSamples = dataSamples;
    }
}

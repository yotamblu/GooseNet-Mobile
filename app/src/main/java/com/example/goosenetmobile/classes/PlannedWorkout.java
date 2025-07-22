package com.example.goosenetmobile.classes;

import java.util.List;

public class PlannedWorkout {
    private String date;
    private String workoutName;
    private String description;
    private List<PlannedInterval> intervals;
    private String coachName;
    private List<String> athleteNames;
    private String workoutId;
    public PlannedWorkout() {
    }

    public PlannedWorkout(String date, String workoutName, String description, List<PlannedInterval> intervals, String coachName, List<String> athleteNames,String workoutId) {
        this.date = date;
        this.workoutName = workoutName;
        this.description = description;
        this.intervals = intervals;
        this.coachName = coachName;
        this.athleteNames = athleteNames;
        this.workoutId = workoutId;
    }


    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PlannedInterval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<PlannedInterval> intervals) {
        this.intervals = intervals;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public List<String> getAthleteNames() {
        return athleteNames;
    }

    public void setAthleteNames(List<String> athleteNames) {
        this.athleteNames = athleteNames;
    }
}

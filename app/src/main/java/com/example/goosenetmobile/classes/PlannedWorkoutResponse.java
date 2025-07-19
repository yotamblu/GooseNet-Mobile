package com.example.goosenetmobile.classes;

public class PlannedWorkoutResponse {
      private PlannedWorkout worokutObject;


      private String plannedWorkoutJson;

      public PlannedWorkoutResponse(){

      }


    public PlannedWorkoutResponse(PlannedWorkout worokutObject, String plannedWorkoutJson) {
        this.worokutObject = worokutObject;
        this.plannedWorkoutJson = plannedWorkoutJson;
    }

    public PlannedWorkout getWorokutObject() {
        return worokutObject;
    }

    public void setWorokutObject(PlannedWorkout worokutObject) {
        this.worokutObject = worokutObject;
    }

    public String getPlannedWorkoutJson() {
        return plannedWorkoutJson;
    }

    public void setPlannedWorkoutJson(String plannedWorkoutJson) {
        this.plannedWorkoutJson = plannedWorkoutJson;
    }
}

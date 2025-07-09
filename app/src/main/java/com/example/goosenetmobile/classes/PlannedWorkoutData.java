package com.example.goosenetmobile.classes;

public class PlannedWorkoutData {

    private String targetName;
    private boolean isFlock;
    private String jsonBody;
    private String date;


    public PlannedWorkoutData(){

    }


    public PlannedWorkoutData(String targetName, boolean isFlock, String jsonBody, String date) {
        this.targetName = targetName;
        this.isFlock = isFlock;
        this.jsonBody = jsonBody;
        this.date = date;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public boolean isFlock() {
        return isFlock;
    }

    public void setFlock(boolean flock) {
        isFlock = flock;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

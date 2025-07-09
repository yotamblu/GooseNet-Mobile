package com.example.goosenetmobile.classes;

public class AddToFlockData {
    private String athleteUserName;
    private String flockName;

    public AddToFlockData(){

    }

    public AddToFlockData(String athleteUserName, String flockName) {
        this.athleteUserName = athleteUserName;
        this.flockName = flockName;
    }

    public String getAthleteUserName() {
        return athleteUserName;
    }

    public void setAthleteUserName(String athleteUserName) {
        this.athleteUserName = athleteUserName;
    }

    public String getFlockName() {
        return flockName;
    }

    public void setFlockName(String flockName) {
        this.flockName = flockName;
    }
}

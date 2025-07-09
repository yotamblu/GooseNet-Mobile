package com.example.goosenetmobile.classes;

public class AthleteCard {
    private String athleteName;
    private String imageData;

    public AthleteCard(String athleteName, String imageData) {
        this.athleteName = athleteName;
        this.imageData = imageData;
    }

    public AthleteCard() {
    }

    public String getAthleteName() {
        return athleteName;
    }

    public void setAthleteName(String athleteName) {
        this.athleteName = athleteName;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }
}

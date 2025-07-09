package com.example.goosenetmobile.classes;

import java.util.List;

public class GetAthletesResponseData {
    public List<AthleteCard> athletesData;

    public List<AthleteCard> getAthletesData() {
        return athletesData;
    }

    public void setAthletesData(List<AthleteCard> athletesData) {
        this.athletesData = athletesData;
    }

    public GetAthletesResponseData(){

    }
}

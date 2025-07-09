package com.example.goosenetmobile.classes;

import java.util.ArrayList;
import java.util.List;

public class Workout {
    public String name;
    public String description;
    public String date;
    public List<Interval> intervals;

    public Workout(String name, String description, String date) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.intervals = new ArrayList<>();
    }

    public void addInterval(Interval interval) {
        intervals.add(interval);
    }
}

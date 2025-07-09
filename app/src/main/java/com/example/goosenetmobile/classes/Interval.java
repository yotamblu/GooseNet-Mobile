package com.example.goosenetmobile.classes;

import java.util.ArrayList;
import java.util.List;

public class Interval {
    public List<SubInterval> subIntervals;

    public Interval() {
        this.subIntervals = new ArrayList<>();
    }

    public void addSubInterval(SubInterval sub) {
        subIntervals.add(sub);
    }
}

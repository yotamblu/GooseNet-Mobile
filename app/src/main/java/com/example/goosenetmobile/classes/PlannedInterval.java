package com.example.goosenetmobile.classes;

import java.util.List;

public class PlannedInterval {
    private int stepOrder;
    private int repeatValue;
    private String type;
    private List<PlannedInterval> steps;
    private String description;
    private String durationType;
    private double durationValue;
    private String intensity;
    private String targetType = "PACE";
    private double targetValueLow;
    private double targetValueHigh;
    private String repeatType;

    public PlannedInterval() {
    }

    public PlannedInterval(int stepOrder, int repeatValue, String type, List<PlannedInterval> steps, String description,
                           String durationType, double durationValue, String intensity, String targetType,
                           double targetValueLow, double targetValueHigh, String repeatType) {
        this.stepOrder = stepOrder;
        this.repeatValue = repeatValue;
        this.type = type;
        this.steps = steps;
        this.description = description;
        this.durationType = durationType;
        this.durationValue = durationValue;
        this.intensity = intensity;
        this.targetType = targetType;
        this.targetValueLow = targetValueLow;
        this.targetValueHigh = targetValueHigh;
        this.repeatType = repeatType;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    public int getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(int repeatValue) {
        this.repeatValue = repeatValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PlannedInterval> getSteps() {
        return steps;
    }

    public void setSteps(List<PlannedInterval> steps) {
        this.steps = steps;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public double getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(double durationValue) {
        this.durationValue = durationValue;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public double getTargetValueLow() {
        return targetValueLow;
    }

    public void setTargetValueLow(double targetValueLow) {
        this.targetValueLow = targetValueLow;
    }

    public double getTargetValueHigh() {
        return targetValueHigh;
    }

    public void setTargetValueHigh(double targetValueHigh) {
        this.targetValueHigh = targetValueHigh;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }
}

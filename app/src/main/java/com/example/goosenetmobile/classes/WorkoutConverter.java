package com.example.goosenetmobile.classes;

import java.util.ArrayList;
import java.util.List;

public class WorkoutConverter {

    // Define fixed duration and pace for ALL REST intervals
    private static final double FIXED_REST_DURATION_SECONDS = 15.0; // E.g., 15 seconds for a brief pause
    private static final double FIXED_REST_PACE_MIN_KM = 20.0;     // E.g., 20 min/km, representing a very slow walk/stop

    public static List<FinalLap> convertPlannedIntervalsToFinalLaps(List<PlannedInterval> plannedIntervals) {
        List<FinalLap> finalLaps = new ArrayList<>();
        for (PlannedInterval interval : plannedIntervals) {
            processInterval(interval, finalLaps);
        }
        return finalLaps;
    }

    private static void processInterval(PlannedInterval interval, List<FinalLap> finalLaps) {
        if ("WorkoutStep".equals(interval.getType())) {
            FinalLap finalLap = createFinalLap(interval);
            finalLaps.add(finalLap);
        } else if ("WorkoutRepeatStep".equals(interval.getType())) {
            for (int i = 0; i < interval.getRepeatValue(); i++) {
                if (interval.getSteps() != null) {
                    for (PlannedInterval step : interval.getSteps()) {
                        processInterval(step, finalLaps);
                    }
                }
            }
        }
    }

    private static FinalLap createFinalLap(PlannedInterval plannedInterval) {
        double calculatedPaceInMinKm;
        double calculatedDurationInSeconds;
        double calculatedDistanceInKilometers;

        if ("REST".equals(plannedInterval.getIntensity())) {
            // All REST laps get the fixed duration and pace
            calculatedDurationInSeconds = FIXED_REST_DURATION_SECONDS;
            calculatedPaceInMinKm = FIXED_REST_PACE_MIN_KM;
            // Distance is then derived from the fixed duration and pace
            calculatedDistanceInKilometers = calculateLapDistanceInKilometers(calculatedDurationInSeconds, calculatedPaceInMinKm);
        } else if ("TIME".equals(plannedInterval.getDurationType())) {
            calculatedDurationInSeconds = plannedInterval.getDurationValue();
            calculatedPaceInMinKm = calculateLapPaceInMinKm(plannedInterval); // Calculate pace from PlannedInterval
            calculatedDistanceInKilometers = calculateLapDistanceInKilometers(calculatedDurationInSeconds, calculatedPaceInMinKm);
        } else { // DurationType is DISTANCE (for non-REST intervals)
            calculatedDistanceInKilometers = plannedInterval.getDurationValue() / 1000.0; // Convert meters to kilometers
            calculatedPaceInMinKm = calculateLapPaceInMinKm(plannedInterval); // Calculate pace from PlannedInterval
            calculatedDurationInSeconds = calculateLapDurationInSeconds(calculatedDistanceInKilometers, calculatedPaceInMinKm);
        }

        // Corrected instantiation of FinalLap with appropriate casting
        return new FinalLap(
                (float) calculatedDistanceInKilometers,
                (int) Math.round(calculatedDurationInSeconds), // Round to nearest int for duration
                (float) calculatedPaceInMinKm,
                0 // avgHeartRate, assuming it's always 0 for planned intervals
        );
    }

    private static double calculateLapPaceInMinKm(PlannedInterval plannedInterval) {
        // targetValueHigh is assumed to be in m/s based on previous context.
        // Convert m/s to min/km
        if (plannedInterval.getTargetValueHigh() > 0) {
            return (1.0 / plannedInterval.getTargetValueHigh()) * (1000.0 / 60.0);
        }
        return 0.0; // Represents an extremely slow or infinite pace.
    }

    private static double calculateLapDurationInSeconds(double lapDistanceInKilometers, double lapPaceInMinKm) {
        if (lapPaceInMinKm > 0) {
            return lapDistanceInKilometers * lapPaceInMinKm * 60.0;
        }
        return 0.0;
    }

    private static double calculateLapDistanceInKilometers(double lapDurationInSeconds, double lapPaceInMinKm) {
        if (lapPaceInMinKm > 0) {
            return (lapDurationInSeconds / 60.0) / lapPaceInMinKm;
        }
        return 0.0;
    }
}
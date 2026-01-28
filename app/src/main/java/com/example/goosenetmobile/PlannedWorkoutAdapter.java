package com.example.goosenetmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.goosenetmobile.R;
import com.example.goosenetmobile.classes.FinalLap;
import com.example.goosenetmobile.classes.PlannedInterval;
import com.example.goosenetmobile.classes.PlannedWorkout;
import com.example.goosenetmobile.CustomLapChartView;
import com.example.goosenetmobile.classes.WorkoutConverter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PlannedWorkoutAdapter extends BaseAdapter {






    private static final String TAG = "IntervalConverter";

    /**
     * Converts a List of PlannedInterval objects to a List of FinalLap objects.
     * Assumes targetType is always "PACE".
     *
     * @param plannedIntervals The list of PlannedInterval objects to convert.
     * @return A new List of FinalLap objects.
     */
    public static List<FinalLap> convertPlannedIntervalsToFinalLaps(List<PlannedInterval> plannedIntervals) {
        List<FinalLap> finalLaps = new ArrayList<>();

        if (plannedIntervals == null || plannedIntervals.isEmpty()) {
            Log.d(TAG, "PlannedIntervals list is null or empty. Returning empty list.");
            return finalLaps;
        }

        for (PlannedInterval interval : plannedIntervals) {
            float lapDistanceInKilometers = 0.0f;
            int lapDurationInSeconds = 0;
            float lapPaceInMinKm = 0.0f;
            int avgHeartRate = 0; // Default or placeholder as no HR in PlannedInterval

            Log.d(TAG, "======================================================");
            Log.d(TAG, "--- Processing PlannedInterval (Step Order: " + interval.getStepOrder() + ") ---");
            Log.d(TAG, "  Raw PlannedInterval Object Values:");
            Log.d(TAG, "    stepOrder: " + interval.getStepOrder());
            Log.d(TAG, "    repeatValue: " + interval.getRepeatValue());
            Log.d(TAG, "    type: " + interval.getType());
            Log.d(TAG, "    steps (size): " + (interval.getSteps() != null ? interval.getSteps().size() : 0));
            Log.d(TAG, "    description: " + interval.getDescription());
            Log.d(TAG, "    durationType: " + interval.getDurationType());
            Log.d(TAG, "    durationValue: " + interval.getDurationValue());
            Log.d(TAG, "    intensity: " + interval.getIntensity());
            Log.d(TAG, "    targetType: " + interval.getTargetType());
            Log.d(TAG, "    targetValueLow: " + interval.getTargetValueLow());
            Log.d(TAG, "    targetValueHigh: " + interval.getTargetValueHigh());
            Log.d(TAG, "    repeatType: " + interval.getRepeatType());
            Log.d(TAG, "------------------------------------------------------");


            // Calculate lapPaceInMinKm from targetValueHigh (m/s to min/km)
            if (interval.getTargetValueHigh() > 0) {
                double speedKmH = interval.getTargetValueHigh() * 3.6;
                if (speedKmH > 0) {
                    lapPaceInMinKm = (float) ((1.0 / speedKmH) * 60.0);
                    Log.d(TAG, "  Calculated lapPaceInMinKm: " + lapPaceInMinKm + " min/km");
                } else {
                    Log.e(TAG, "  Error: Calculated speedKmH is zero or negative (" + speedKmH + ") for targetValueHigh: " + interval.getTargetValueHigh());
                }
            } else {
                Log.e(TAG, "  Error: TargetValueHigh is zero or negative (" + interval.getTargetValueHigh() + "). Cannot calculate pace. Pace set to 0.0.");
            }

            // Determine lapDurationInSeconds and lapDistanceInKilometers based on durationType
            if (interval.getDurationType() != null) {
                switch (interval.getDurationType().toUpperCase()) {
                    case "TIME":
                        lapDurationInSeconds = (int) interval.getDurationValue();
                        Log.d(TAG, "  DurationType is TIME. Initial lapDurationInSeconds (from durationValue): " + lapDurationInSeconds + "s");

                        if (lapPaceInMinKm > 0 && lapDurationInSeconds > 0) {
                            double speedKmPerSecond = 1.0 / (lapPaceInMinKm * 60.0);
                            lapDistanceInKilometers = (float) (speedKmPerSecond * lapDurationInSeconds);
                            Log.d(TAG, "  Calculated lapDistanceInKilometers (from TIME and PACE): " + lapDistanceInKilometers + " km");
                        } else {
                            Log.w(TAG, "  Warning: Cannot calculate distance for TIME interval. Pace (" + lapPaceInMinKm + ") or duration (" + lapDurationInSeconds + ") is zero/negative. Distance set to 0.0.");
                        }
                        break;
                    case "DISTANCE":
                        lapDistanceInKilometers = (float) (interval.getDurationValue() / 1000.0);
                        Log.d(TAG, "  DurationType is DISTANCE. Initial lapDistanceInKilometers (from durationValue): " + lapDistanceInKilometers + " km");

                        if (lapPaceInMinKm > 0 && lapDistanceInKilometers > 0) {
                            lapDurationInSeconds = (int) ((lapPaceInMinKm * lapDistanceInKilometers) * 60.0);
                            Log.d(TAG, "  Calculated lapDurationInSeconds (from DISTANCE and PACE): " + lapDurationInSeconds + "s");
                        } else {
                            Log.w(TAG, "  Warning: Cannot calculate duration for DISTANCE interval. Pace (" + lapPaceInMinKm + ") or distance (" + lapDistanceInKilometers + ") is zero/negative. Duration set to 0.");
                        }
                        break;
                    case "OPEN":
                        Log.d(TAG, "  DurationType is OPEN. Duration/Distance will remain 0 unless explicit values are desired for OPEN intervals.");
                        break;
                    default:
                        Log.e(TAG, "  Error: Unsupported or unhandled DurationType: " + interval.getDurationType() + ". Duration/Distance remain 0.");
                        break;
                }
            } else {
                Log.e(TAG, "  Error: DurationType is null for interval: " + interval.getDescription() + ". Duration/Distance remain 0.");
            }

            // Decide whether to add the FinalLap for the current interval
            // Only add if it's not a container type and has valid values
            boolean isContainerType = (interval.getType() != null &&
                    (interval.getType().equalsIgnoreCase("REPEAT") ||
                            interval.getType().equalsIgnoreCase("WORKOUTSEGMENT") ||
                            interval.getType().equalsIgnoreCase("WORKOUTREPEATSTEP")));

            if (!isContainerType && (lapDurationInSeconds > 0 || lapDistanceInKilometers > 0) && lapPaceInMinKm > 0) {
                FinalLap newLap = new FinalLap(
                        lapDistanceInKilometers,
                        lapDurationInSeconds,
                        lapPaceInMinKm,
                        avgHeartRate
                );
                finalLaps.add(newLap);
                Log.d(TAG, "  --> FinalLap ADDED for '" + interval.getDescription() + "':");
                Log.d(TAG, "      lapDistanceInKilometers: " + newLap.getLapDistanceInKilometers() + " km");
                Log.d(TAG, "      lapDurationInSeconds: " + newLap.getLapDurationInSeconds() + " s");
                Log.d(TAG, "      lapPaceInMinKm: " + newLap.getLapPaceInMinKm() + " min/km");
                Log.d(TAG, "      avgHeartRate: " + newLap.getAvgHeartRate());
            } else {
                Log.d(TAG, "  Skipped adding FinalLap for '" + interval.getDescription() + "' (Is Container Type: " + isContainerType + ", Calculated Duration: " + lapDurationInSeconds + "s, Distance: " + lapDistanceInKilometers + "km, Pace: " + lapPaceInMinKm + "min/km)");
            }

            // Handle repetitions for container types with steps
            if (interval.getSteps() != null && !interval.getSteps().isEmpty()) {
                if (isContainerType) { // Use the boolean flag for consistency
                    int repetitions = 1; // Default to 1 if not a repeat type or repeatValue is 0
                    if (interval.getType() != null &&
                            (interval.getType().equalsIgnoreCase("REPEAT") ||
                                    interval.getType().equalsIgnoreCase("WORKOUTREPEATSTEP"))) { // Only these types get repeated
                        repetitions = interval.getRepeatValue() > 0 ? interval.getRepeatValue() : 1;
                    }

                    Log.d(TAG, "  Processing nested steps for interval type: " + interval.getType() +
                            " (Step Order: " + interval.getStepOrder() + ") for " + repetitions + " repetitions.");

                    for (int i = 0; i < repetitions; i++) {
                        Log.d(TAG, "    --- Starting repetition " + (i + 1) + " of " + repetitions + " for steps of '" + interval.getDescription() + "' ---");
                        // Recursively call for the steps list
                        finalLaps.addAll(convertPlannedIntervalsToFinalLaps(interval.getSteps()));
                        Log.d(TAG, "    --- Finished repetition " + (i + 1) + " ---");
                    }
                } else {
                    Log.d(TAG, "  Interval type '" + interval.getType() + "' has steps but is not a recognized repeating container type. Skipping direct nested steps conversion for FinalLap.");
                }
            }
            Log.d(TAG, "======================================================");
        }
        return finalLaps;
    }


    /**
     * Calculates pace in min/km from duration in seconds and distance in kilometers.
     * Returns 0 if distance is 0 to avoid division by zero.
     *
     * @param durationSec Duration in seconds.
     * @param distanceKm  Distance in kilometers.
     * @return Pace in minutes per kilometer.
     */
    private static float calculatePaceFromDurationAndDistance(int durationSec, float distanceKm) {
        if (distanceKm > 0) {
            return (float) durationSec / 60f / distanceKm;
        }
        return 0f; // Can't calculate pace if no distance
    }


    public static double minKmToMetersPerSecond(double paceMinKm) {
        // Convert pace from float minutes to total seconds per km
        double totalSeconds = paceMinKm * 60.0;
        // speed = distance / time = 1000 meters / totalSeconds seconds
        return 1000.0 / totalSeconds;
    }

    /**
     * Estimates duration in seconds given distance in kilometers and pace in min/km.
     *
     * @param distanceKm       Distance in kilometers.
     * @param paceMinPerKm     Pace in minutes per kilometer.
     * @return Estimated duration in seconds.
     */
    private static double calculateDuration(float distanceKm, double paceMinPerKm) {
        if (paceMinPerKm <= 0) {
            return 0; // If pace is 0 or negative, time is undefined or infinite.
        }
        return distanceKm * paceMinPerKm * 60; // km * (min/km) * (60 sec/min)
    }

    /**
     * Estimates distance in kilometers given duration in seconds and pace in min/km.
     *
     * @param durationSec      Duration in seconds.
     * @param paceMinPerKm     Pace in minutes per kilometer.
     * @return Estimated distance in kilometers.
     */
    private static double calculateDistance(int durationSec, double paceMinPerKm) {
        if (paceMinPerKm <= 0) {
            return 0; // If pace is 0, distance is 0 for any finite time.
        }
        return (double) durationSec / 60.0 / paceMinPerKm; // (seconds / 60) / (min/km) = minutes / (min/km) = km
    }

    // Estimate time from pace range (min/km) and distance
    private static int estimateTimeFromPace(float distanceKm, double paceLow, double paceHigh) {
        double avgPace = (paceLow + paceHigh) / 2.0;
        return (int) (avgPace * 60 * distanceKm);
    }

    // Estimate distance from pace and duration
    private static float estimateDistanceFromPace(int durationSec, double paceLow, double paceHigh) {
        double avgPace = (paceLow + paceHigh) / 2.0;
        return (float) ((durationSec / 60.0) / avgPace);
    }


    private Context context;
    private List<PlannedWorkout> workoutList;
    private LayoutInflater inflater;

    public PlannedWorkoutAdapter(Context context, List<PlannedWorkout> workoutList) {
        this.context = context;
        this.workoutList = workoutList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return workoutList.size();
    }

    @Override
    public Object getItem(int position) {
        return workoutList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView coachProfilePic;
        TextView coachName;
        CustomLapChartView lapChartView;
        CardView mainCardView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.planned_workout_card, parent, false);
            holder = new ViewHolder();
            holder.coachProfilePic = convertView.findViewById(R.id.coachProfilePic);
            holder.coachName = convertView.findViewById(R.id.coachName);
            holder.lapChartView = convertView.findViewById(R.id.lapChartView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PlannedWorkout workout = workoutList.get(position);

        // Set coach name
        holder.coachName.setText(workout.getCoachName());

        // Set profile pic - you can load from Base64 or leave it as default drawable
        final Bitmap[] profilePicBitmap = {null};
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() ->{
            profilePicBitmap[0] = ApiService.getProfilePicBitmap(workout.getCoachName());
            latch.countDown();
        }).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        holder.coachProfilePic.setImageBitmap(profilePicBitmap[0]);
        List<FinalLap> laps = WorkoutConverter.convertPlannedIntervalsToFinalLaps(workout.getIntervals());

        holder.lapChartView.setLaps(laps,null);

        holder.mainCardView = convertView.findViewById(R.id.plannedWorkoutCard);
        holder.mainCardView.setOnClickListener(v ->{
            Intent intent = new Intent(context,PlannedWorkoutActivity.class);
            intent.putExtra("workoutId",workout.getWorkoutId());
            intent.putExtra("coachName",workout.getCoachName());
            context.startActivity(intent);
        });

        return convertView;
    }



    // Optional helper to decode base64 image string if added in future
    private Bitmap decodeBase64Image(String base64) {
        byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
    }
}

package com.example.goosenetmobile;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goosenetmobile.classes.FinalLap;
import com.example.goosenetmobile.classes.PlannedInterval;
import com.example.goosenetmobile.classes.PlannedWorkout;
import com.example.goosenetmobile.classes.PlannedWorkoutResponse;
import com.example.goosenetmobile.classes.WorkoutConverter;

import java.util.ArrayList;
import java.util.List;

public class PlannedWorkoutActivity extends AppCompatActivity {

    // Workout Date
    TextView textViewDate;

    // Workout Name
    TextView textViewWorkoutName;

    // Workout Description
    TextView textViewDescription;

    // Coach Profile Picture
    de.hdodenhof.circleimageview.CircleImageView imageViewCoach;

    // Coach Name
    TextView textViewCoachName;

    // Lap Chart View
    com.example.goosenetmobile.CustomLapChartView lapChartView;

    // Textual Workout Details
    TextView textViewWorkoutDetails;
    Dialog progressDialog;

    private static final String TAG = "IntervalConverter";


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


    private void showBlockingProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View view = LayoutInflater.from(this).inflate(R.layout.fetching_workout_data_progress_loader, null);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false); // Block back button
            progressDialog.setContentView(view);
        }
        progressDialog.show();
    }

    public void hideBlockingProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void InitializeViews() {
        textViewDate = findViewById(R.id.textViewDate);
        textViewWorkoutName = findViewById(R.id.textViewWorkoutName);
        textViewDescription = findViewById(R.id.textViewDescription);
        imageViewCoach = findViewById(R.id.imageViewCoach);
        textViewCoachName = findViewById(R.id.textViewCoachName);
        lapChartView = findViewById(R.id.lapChartView);
        textViewWorkoutDetails = findViewById(R.id.textViewWorkoutDetails);


    }

    public static List<FinalLap> convertToFinalLaps(List<PlannedInterval> plannedIntervals) {
        List<FinalLap> finalLaps = new ArrayList<>();
        flattenAndConvert(plannedIntervals, finalLaps);
        return finalLaps;
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
    private static void flattenAndConvert(List<PlannedInterval> intervals, List<FinalLap> result) {
        if (intervals == null) return;

        for (PlannedInterval interval : intervals) {
            if ("WorkoutRepeatStep".equals(interval.getType())) {
                for (int i = 0; i < interval.getRepeatValue(); i++) {
                    flattenAndConvert(interval.getSteps(), result);
                }
            } else if ("WorkoutStep".equals(interval.getType())) {
                float distanceKm = 0f;
                int durationSec = 0;

                if ("DISTANCE".equalsIgnoreCase(interval.getDurationType())) {
                    distanceKm = (float) (interval.getDurationValue() / 1000.0);
                    durationSec = estimateTimeFromPace(distanceKm, interval.getTargetValueLow(), interval.getTargetValueHigh());
                } else if ("TIME".equalsIgnoreCase(interval.getDurationType())) {
                    durationSec = (int) interval.getDurationValue();
                    distanceKm = estimateDistanceFromPace(durationSec, interval.getTargetValueLow(), interval.getTargetValueHigh());
                }

                float paceMinPerKm = distanceKm > 0 ? (float) durationSec / 60f / distanceKm : 0f;

                FinalLap lap = new FinalLap(distanceKm, durationSec, paceMinPerKm, 0); // HR = 0 by default
                result.add(lap);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planned_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitializeViews();
        showBlockingProgressDialog();
        String workoutId = getIntent().getStringExtra("workoutId");
        Bitmap profilePicData = (Bitmap) getIntent().getParcelableExtra("profilePicData");
        imageViewCoach.setImageBitmap(profilePicData);
        new Thread(() ->{
            PlannedWorkoutResponse workoutData = ApiService.getPlannedWorkoutById(workoutId);
            String workoutMessage  = workoutData.getPlannedWorkoutJson();
            PlannedWorkout workoutObject = workoutData.getWorokutObject();
            runOnUiThread(() ->{
                textViewDate.setText(workoutObject.getDate());
                textViewWorkoutName.setText(workoutObject.getWorkoutName());
                textViewDescription.setText(workoutObject.getDescription());
                textViewCoachName.setText(workoutObject.getCoachName());
                textViewWorkoutDetails.setText(workoutMessage);
                lapChartView.setLaps(WorkoutConverter.convertPlannedIntervalsToFinalLaps(workoutObject.getIntervals()),PlannedWorkoutActivity.this);
            });
        }).start();

    }
}
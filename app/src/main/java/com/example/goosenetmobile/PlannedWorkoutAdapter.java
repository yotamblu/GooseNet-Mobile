package com.example.goosenetmobile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.goosenetmobile.R;
import com.example.goosenetmobile.classes.FinalLap;
import com.example.goosenetmobile.classes.PlannedInterval;
import com.example.goosenetmobile.classes.PlannedWorkout;
import com.example.goosenetmobile.CustomLapChartView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class PlannedWorkoutAdapter extends BaseAdapter {
    public static List<FinalLap> convertToFinalLaps(List<PlannedInterval> plannedIntervals) {
        List<FinalLap> finalLaps = new ArrayList<>();
        flattenAndConvert(plannedIntervals, finalLaps);
        return finalLaps;
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
        List<FinalLap> laps = convertToFinalLaps(workout.getIntervals());
        holder.lapChartView.setLaps(laps,null);




        return convertView;
    }

    // Optional helper to decode base64 image string if added in future
    private Bitmap decodeBase64Image(String base64) {
        byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
    }
}

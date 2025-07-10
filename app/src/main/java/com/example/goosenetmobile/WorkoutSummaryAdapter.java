package com.example.goosenetmobile;


import static com.bumptech.glide.load.resource.drawable.DrawableDecoderCompat.getDrawable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.goosenetmobile.R;
import com.example.goosenetmobile.classes.WorkoutSummary;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WorkoutSummaryAdapter extends BaseAdapter {

    private final Context context;
    private final List<WorkoutSummary> workoutList;
    private final LayoutInflater inflater;

    public WorkoutSummaryAdapter(Context context, List<WorkoutSummary> workoutList) {
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
        return workoutList.get(position).getWorkoutId();
    }
    public static Pair<GeoPoint, Double> getBestCenterAndZoom(List<GeoPoint> points) {
        if (points == null || points.isEmpty()) return null;

        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        for (GeoPoint p : points) {
            minLat = Math.min(minLat, p.getLatitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            minLon = Math.min(minLon, p.getLongitude());
            maxLon = Math.max(maxLon, p.getLongitude());
        }

        double centerLat = (minLat + maxLat) / 2;
        double centerLon = (minLon + maxLon) / 2;
        GeoPoint center = new GeoPoint(centerLat, centerLon);

        // Estimate zoom level based on bounding box size
        double latSpan = maxLat - minLat;
        double lonSpan = maxLon - minLon;
        double maxSpan = Math.max(latSpan, lonSpan);

        // Heuristic: smaller span = higher zoom
        double zoom;
        if (maxSpan < 0.01) zoom = 17;
        else if (maxSpan < 0.05) zoom = 15;
        else if (maxSpan < 0.1) zoom = 13;
        else if (maxSpan < 0.5) zoom = 11;
        else if (maxSpan < 1) zoom = 9;
        else if (maxSpan < 2) zoom = 8;
        else zoom = 6;

        return new Pair<>(center, zoom);
    }


    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size, size);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);

        return output;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.workout_summary_card, parent, false);
            holder = new ViewHolder();

            holder.profilePicImageView = convertView.findViewById(R.id.profilePicImageView);
            holder.athleteNameTextView = convertView.findViewById(R.id.athleteNameTextView);
            holder.workoutDateTextView = convertView.findViewById(R.id.workoutDateTextView);
            holder.workoutNameTextView = convertView.findViewById(R.id.workoutNameTextView);
            holder.workoutDistanceTextView = convertView.findViewById(R.id.workoutDistanceTextView);
            holder.workoutDurationTextView = convertView.findViewById(R.id.workoutDurationTextView);
            holder.workoutAvgPaceTextView = convertView.findViewById(R.id.workoutAvgPaceTextView);
            holder.workoutAvgHeartRateTextView = convertView.findViewById(R.id.workoutAvgHeartRateTextView);
            holder.workoutMapView = convertView.findViewById(R.id.workoutMapView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WorkoutSummary workout = workoutList.get(position);

        // For demo, no dynamic profile pic — set default
        holder.profilePicImageView.setImageResource(R.drawable.ic_profile);

        // Hardcoded athlete name — replace with real data if available
        holder.athleteNameTextView.setText(workout.getAthleteName());

        // MapView - Placeholder only (you would load the workoutCoordsJsonStr and draw on MapView here)
        // For now, just clear overlays or add basic setup
        holder.workoutMapView.getOverlays().clear();
        holder.workoutMapView.invalidate();

        // Workout Date
        if (!TextUtils.isEmpty(workout.getWorkoutDate())) {
            holder.workoutDateTextView.setText("Ran on " + workout.getWorkoutDate());
        } else {
            holder.workoutDateTextView.setText("");
        }

        Configuration.getInstance().load(context.getApplicationContext(),((Activity)context).getPreferences(Context.MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(context.getPackageName());

        List<GeoPoint> points = new ArrayList<>();
        JSONArray coordsArray = null;
        try {
            coordsArray = new JSONArray(workout.getWorkoutCoordsJsonStr());
            for (int i = 0; i < coordsArray.length(); i++) {
                JSONArray latLng = coordsArray.getJSONArray(i);
                double lat = latLng.getDouble(0);
                double lon = latLng.getDouble(1);
                if(lat != 0 && lon != 0){
                    points.add(new GeoPoint(lat, lon));

                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        Pair<GeoPoint, Double> centerAndZoom = getBestCenterAndZoom(points);

        // Center the map on a location
        holder.workoutMapView.getController().setCenter(centerAndZoom.first);
        holder.workoutMapView.getController().setZoom(centerAndZoom.second -0.3);
        Polyline polyline = new Polyline();
        polyline.setPoints(points);
        polyline.setWidth(8f);
        polyline.setColor(Color.BLUE);
        holder.workoutMapView.getOverlayManager().add(polyline);

        // Workout Name
        holder.workoutNameTextView.setText(workout.getWorkoutName() != null ? workout.getWorkoutName() : "");
            
        // Workout Distance in km with 2 decimal places
        double distanceKm = workout.getWorkoutDistanceInMeters() / 1000.0;
        holder.workoutDistanceTextView.setText(String.format("%.2f", distanceKm));
        // Workout Duration formatted mm:ss
        holder.workoutDurationTextView.setText(formatDuration(workout.getWorkoutDurationInSeconds()));

        // Avg Pace in min/km formatted mm:ss (workoutAvgPaceInMinKm assumed to be decimal minutes per km)
        holder.workoutAvgPaceTextView.setText(formatPace(workout.getWorkoutAvgPaceInMinKm()));
        holder.profilePicImageView.setImageBitmap(getCircularBitmap(GooseNetUtil.base64ToBitmap(workout.getProfilePicData())));
        // Avg Heart Rate with bpm suffix
        holder.workoutAvgHeartRateTextView.setText(workout.getWorkoutAvgHR() + " bpm");


        return convertView;
    }

    private static class ViewHolder {
        ImageView profilePicImageView;
        TextView athleteNameTextView;
        TextView workoutDateTextView;
        TextView workoutNameTextView;
        TextView workoutDistanceTextView;
        TextView workoutDurationTextView;
        TextView workoutAvgPaceTextView;
        TextView workoutAvgHeartRateTextView;
        MapView workoutMapView;
        CardView mainLayout;
    }

    private String formatDuration(int totalSeconds) {
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds);
        long seconds = totalSeconds - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String formatPace(double paceDecimalMinutesPerKm) {
        if (paceDecimalMinutesPerKm <= 0) {
            return "--:--";
        }
        int minutes = (int) paceDecimalMinutesPerKm;
        int seconds = (int) Math.round((paceDecimalMinutesPerKm - minutes) * 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
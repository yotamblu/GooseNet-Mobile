package com.example.goosenetmobile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goosenetmobile.classes.ActivityData;
import com.example.goosenetmobile.classes.Workout;
import com.example.goosenetmobile.classes.WorkoutMarkerView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.components.AxisBase;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goosenetmobile.classes.DataSample;
import com.example.goosenetmobile.classes.FinalLap;
import com.example.goosenetmobile.classes.WorkoutExtensiveData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkoutActivity extends AppCompatActivity {


    private String workoutNameStr;
    private long workoutId;
    private int workoutDurationInSeconds;
    private double workoutDistanceInMeters;
    private int workoutAvgHR;
    private double workoutAvgPaceInMinKm;
    private String workoutCoordsJsonStr;
    private String workoutDate;
    private String profilePicData;
    private String athleteNameStr;
    private CircleImageView profileImage;
    private TextView athleteName;
    private TextView workoutDistance;
    private TextView avgPace;
    private TextView avgHeartRate;
    private ImageButton shareButton;

    private org.osmdroid.views.MapView workoutMap;
    private com.example.goosenetmobile.CustomLapChartView lapChartView;
    private TableLayout lapDataTable;

    private com.github.mikephil.charting.charts.LineChart heartRateChart;
    private com.github.mikephil.charting.charts.LineChart paceChart;
    private com.github.mikephil.charting.charts.LineChart elevationChart;
    private Dialog progressDialog;

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
    public void populateElevationChart(LineChart elevationChart, List<DataSample> samples) {
        List<Entry> entries = new ArrayList<>();

        for (DataSample sample : samples) {
            float x = sample.getTimerDurationInSeconds();
            float elevation = (float) sample.getElevationInMeters();

            entries.add(new Entry(x, elevation));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Elevation");
        dataSet.setColor(Color.MAGENTA);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);  // or CUBIC_BEZIER for smoothing
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#33FF00FF"));

        LineData lineData = new LineData(dataSet);
        elevationChart.setData(lineData);

        elevationChart.getDescription().setEnabled(false);
        elevationChart.getLegend().setEnabled(false);

        YAxis left = elevationChart.getAxisLeft();
        left.setDrawGridLines(true);
        left.setTextColor(Color.BLACK);

        // Format elevation as meters (integer)
        left.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format("%.0f m", value);
            }
        });

        elevationChart.getAxisRight().setEnabled(false);

        XAxis xAxis = elevationChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        elevationChart.invalidate();
    }


    public void populateSpeedChart(LineChart speedChart, List<DataSample> samples) {
        List<Entry> entries = new ArrayList<>();

        for (DataSample sample : samples) {
            float x = sample.getTimerDurationInSeconds();
            float speed = (float) sample.getSpeedMetersPerSecond();

            if (speed >= 0) {
                entries.add(new Entry(x, speed));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Speed");
        dataSet.setColor(Color.GREEN);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);  // or CUBIC_BEZIER for smoothing
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#3300FF00"));

        LineData lineData = new LineData(dataSet);
        speedChart.setData(lineData);

        speedChart.getDescription().setEnabled(false);
        speedChart.getLegend().setEnabled(false);

        YAxis left = speedChart.getAxisLeft();
        left.setDrawGridLines(true);
        left.setTextColor(Color.BLACK);

        // Optionally format speed on Y axis, e.g. km/h
        left.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // Convert m/s to km/h
                float kmh = value * 3.6f;
                return String.format("%.1f km/h", kmh);
            }
        });

        speedChart.getAxisRight().setEnabled(false);

        XAxis xAxis = speedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        speedChart.invalidate();
    }


    public void populateHeartRateChart(LineChart chart, List<DataSample> samples) {
        List<Entry> entries = new ArrayList<>();

        for (DataSample sample : samples) {
            entries.add(new Entry(sample.getTimerDurationInSeconds(), sample.getHeartRate()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Heart Rate");
        dataSet.setColor(Color.RED);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // optional smoothing
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#33F44336")); // transparent red fill

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // Optional axis styling
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        chart.getAxisRight().setEnabled(false);

        chart.invalidate(); // refresh chart
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profileImage);
        athleteName = findViewById(R.id.athleteName);
        workoutDistance = findViewById(R.id.workoutDistance);
        avgPace = findViewById(R.id.avgPace);
        avgHeartRate = findViewById(R.id.avgHeartRate);
        shareButton = findViewById(R.id.shareButton);

        workoutMap = findViewById(R.id.workoutMap);
        lapChartView = findViewById(R.id.lapChartView);
        lapDataTable = findViewById(R.id.lapDataTable);
        shareButton = findViewById(R.id.shareButton);
        heartRateChart = findViewById(R.id.heartRateChart);
        paceChart = findViewById(R.id.paceChart);
        elevationChart = findViewById(R.id.elevationChart);
    }

    private String formatDuration(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    private void populateCharts(){
        new Thread(() ->{
            WorkoutExtensiveData data = ApiService.getWorkoutExtensiveData(athleteNameStr,String.valueOf(workoutId));
            List<FinalLap> lapsList = data.getWorkoutLaps();
            lapsList.get(lapsList.size() -1).lapDurationInSeconds /= 100f;
            Log.i("LAPS",new Gson().toJson(lapsList));
            lapChartView.setLaps(lapsList,WorkoutActivity.this);
            // Optional: Add header
            runOnUiThread(() ->{
                // Add header
                TableRow header = new TableRow(this);
                header.addView(makeCell("Lap #", true));
                header.addView(makeCell("Distance", true));
                header.addView(makeCell("Pace", true));
                header.addView(makeCell("HR", true));
                header.addView(makeCell("Time", true));
                lapDataTable.addView(header);
                List<FinalLap> laps = data.getWorkoutLaps();
                for (int i = 0; i < laps.size(); i++) {
                    FinalLap lap = laps.get(i);

                    TableRow row = new TableRow(this);
                    row.addView(makeCell(String.valueOf(i + 1), false));
                    row.addView(makeCell(String.format("%.2f km", lap.getLapDistanceInKilometers()), false));
                    row.addView(makeCell(formatPace(lap.getLapPaceInMinKm()), false));
                    row.addView(makeCell(lap.getAvgHeartRate() + " bpm", false));
                    row.addView(makeCell(formatDuration(lap.getLapDurationInSeconds()), false));

                    lapDataTable.addView(row);

                }
                MarkerView heartMarker = new WorkoutMarkerView(this, R.layout.marker_workout, "heart");
                heartMarker.setChartView(heartRateChart);
                heartRateChart.setMarker(heartMarker);

                MarkerView elevationMarker = new WorkoutMarkerView(this, R.layout.marker_workout, "elevation");
                elevationMarker.setChartView(elevationChart);
                elevationChart.setMarker(elevationMarker);

                MarkerView speedMarker = new WorkoutMarkerView(this, R.layout.marker_workout, "speed");
                speedMarker.setChartView(paceChart);
                paceChart.setMarker(speedMarker);
                 System.out.println(data.getDataSamples().get(1).getElevationInMeters());
                populateHeartRateChart(heartRateChart, data.getDataSamples());
                populateSpeedChart(paceChart,data.getDataSamples());
                populateElevationChart(elevationChart,data.getDataSamples());

            });

        }).start();


    }




    private void populateWorkoutData() {
        // 1. Athlete name
        athleteName.setText(athleteNameStr);

        // 2. Workout distance in km, rounded to 2 decimals
        String formattedDistance = String.format(Locale.getDefault(), "Distance: %.2f km", workoutDistanceInMeters / 1000);
        workoutDistance.setText(formattedDistance);

        // 3. Pace formatted mm:ss
        int totalSeconds = (int) (workoutAvgPaceInMinKm * 60);
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        String paceFormatted = String.format(Locale.getDefault(), "Pace: %02d:%02d", mins, secs);
        avgPace.setText(paceFormatted);

        // 4. Heart rate
        avgHeartRate.setText("Heart Rate: " + workoutAvgHR + " bpm");

        if(getIntent().getData() == null){
            profileImage.setImageBitmap(GooseNetUtil.base64ToBitmap(profilePicData));

        }
        // 5. Profile image (if you base64 encoded it into the intent)

        // 6. Workout map (assumes you have polyline coords as JSON string)
        if (workoutCoordsJsonStr != null && !workoutCoordsJsonStr.isEmpty()) {
            try {
                List<GeoPoint> points = parseJsonToGeoPoints(workoutCoordsJsonStr);
                if (!points.isEmpty()) {
                    Polyline polyline = new Polyline();
                    polyline.setPoints(points);
                    polyline.setColor(Color.BLUE);
                    workoutMap.getOverlays().add(polyline);
                    Pair<GeoPoint,Double> bestCenterAndZoom = getBestCenterAndZoom(points);
                    workoutMap.getController().setZoom(bestCenterAndZoom.second);
                    GeoPoint center =  bestCenterAndZoom.first;
                    workoutMap.getController().setCenter(center);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private TextView makeCell(String text, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setGravity(Gravity.CENTER);
        if (bold) tv.setTypeface(Typeface.DEFAULT_BOLD);
        return tv;
    }

    private String formatPace(float paceMinPerKm) {
        return String.format("%.0f:%02.0f /km", Math.floor(paceMinPerKm), (paceMinPerKm % 1) * 60);
    }


    private List<GeoPoint> parseJsonToGeoPoints(String json) throws JSONException {
        List<GeoPoint> points = new ArrayList<>();
        JSONArray arr = new JSONArray(json);
        for (int i = 0; i < arr.length(); i++) {
            JSONArray pair = arr.getJSONArray(i);
            double lat = pair.getDouble(0);
            double lon = pair.getDouble(1);
            if(lat != 0 && lon != 0){
                points.add(new GeoPoint(lat, lon));

            }
        }
        return points;
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


    private void initializeActivityData(){
        populateWorkoutData();
        populateCharts();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out my GooseNet workout 📈🐦 + https://goosnetcom.bsite.net/workout.aspx?userName=" + athleteNameStr + "&activityId=" + workoutId);
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");

                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(WorkoutActivity.this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        showBlockingProgressDialog();
        Configuration.getInstance().load(getApplicationContext(),getPreferences(Context.MODE_PRIVATE));
        Configuration.getInstance().setUserAgentValue(getPackageName());
        initializeViews();

        Intent intent = getIntent();
        if(intent.getData() != null){
            String url = intent.getData().toString();
            new Thread(() ->{
                System.out.println(intent.getData().toString());
                String athleteName = intent.getData().getQueryParameter("athleteName");
                String id =intent.getData().getQueryParameter("id");
                ActivityData workoutData = null;
                if (athleteName != null && id != null) {
                     workoutData = ApiService.getWorkoutById(athleteName, id);
                } else {
                    // Handle error: missing parameters
                    Log.e("WorkoutDeepLink", "Missing athleteName or id in URI: " + url);
                }
                workoutNameStr = workoutData.getWokroutName();
                Log.i("DID GET WORKOUT DATA ",String.valueOf(workoutData == null));
                workoutId = Long.parseLong(id);
                workoutDurationInSeconds = workoutData.getWorkoutDurationInSeconds();
                workoutDistanceInMeters = workoutData.getWorkoutDistanceInMeters();
                workoutAvgHR = workoutData.getWorkoutAvgHR();
                workoutAvgPaceInMinKm = workoutData.getWorkoutAvgPaceInMinKm();
                workoutCoordsJsonStr = workoutData.getWorkoutCoordsJsonStr();
                workoutDate = workoutData.getWorkoutDate();
                athleteNameStr =athleteName;


                runOnUiThread(() ->{
                    profileImage.setImageBitmap(ApiService.getProfilePicBitmap(athleteNameStr));
                    initializeActivityData();

                });




            }).start();


        }else {
            workoutNameStr = intent.getStringExtra("workoutName");
            workoutId = intent.getLongExtra("workoutId", -1L);
            workoutDurationInSeconds = intent.getIntExtra("workoutDurationInSeconds", 0);
            workoutDistanceInMeters = intent.getDoubleExtra("workoutDistanceInMeters", 0.0);
            workoutAvgHR = intent.getIntExtra("workoutAvgHR", 0);
            workoutAvgPaceInMinKm = intent.getDoubleExtra("workoutAvgPaceInMinKm", 0.0);
            workoutCoordsJsonStr = intent.getStringExtra("workoutCoordsJsonStr");
            workoutDate = intent.getStringExtra("workoutDate");
            profilePicData = intent.getStringExtra("profilePicData");
            athleteNameStr = intent.getStringExtra("athleteName");

            initializeActivityData();


        }
    }
}
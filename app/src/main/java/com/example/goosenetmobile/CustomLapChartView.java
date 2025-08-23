package com.example.goosenetmobile;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.example.goosenetmobile.classes.FinalLap;
import com.example.goosenetmobile.classes.Workout;


import java.util.ArrayList;
import java.util.List;


public class CustomLapChartView extends View {


    private static final String TAG = "CustomLapChartView";


    private List<FinalLap> laps = new ArrayList<>();


    private static final float MIN_BAR_HEIGHT_PX = 24f;


    public CustomLapChartView(Context context) {
        super(context);
    }


    public CustomLapChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public CustomLapChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setLaps(List<FinalLap> laps, Activity activityInstance) {
        this.laps = laps != null ? laps : new ArrayList<>();

        invalidate();
        try {
            if (activityInstance instanceof WorkoutActivity){
                ((WorkoutActivity) activityInstance).hideBlockingProgressDialog();


            }else if(activityInstance instanceof PlannedWorkoutActivity){
                ((PlannedWorkoutActivity) activityInstance).hideBlockingProgressDialog();
            }
        }
        catch (Exception ignored){


        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int viewWidth = getWidth();
        int viewHeight = getHeight();


        Log.d(TAG, "View width: " + viewWidth + ", height: " + viewHeight);


        if (viewWidth <= 0 || viewHeight <= 0) {
            Log.w(TAG, "View dimensions not valid for drawing.");
            return;
        }
        if (laps == null || laps.isEmpty()) {
            Log.w(TAG, "No laps to draw.");
            return;
        }


        List<Float> speeds = new ArrayList<>();
        float minSpeed = Float.MAX_VALUE;
        float maxSpeed = Float.MIN_VALUE;


        for (int i = 0; i < laps.size(); i++) {
            FinalLap lap = laps.get(i);
            float duration = lap.getLapDurationInSeconds();
            float distanceMeters = lap.getLapDistanceInKilometers() * 1000f;
            float speed = (duration > 0) ? (distanceMeters / duration) : 0f;
            speeds.add(speed);


            if (speed < minSpeed) minSpeed = speed;
            if (speed > maxSpeed) maxSpeed = speed;


            Log.d(TAG, "Lap " + i + " duration: " + duration + ", speed: " + speed);
        }
        if (minSpeed == maxSpeed) maxSpeed = minSpeed + 1f; // avoid division by zero


        List<Float> idealWidths = new ArrayList<>();
        float sumIdealWidths = 0f;


        for (int i = 0; i < laps.size(); i++) {
            float dur = laps.get(i).getLapDurationInSeconds();
            if (dur <= 0f) dur = 1f;




            idealWidths.add(dur);
            sumIdealWidths += dur;
        }


        Log.d(TAG, "Sum ideal widths (durations): " + sumIdealWidths);


        float scale = viewWidth / sumIdealWidths;
        Log.d(TAG, "Scale factor (pixels per duration unit): " + scale);


        float left = 0f;
        Paint paint = new Paint();


        for (int i = 0; i < laps.size(); i++) {
            float lapWidth = idealWidths.get(i) * scale;
            if (i == laps.size() - 1) {
                lapWidth = viewWidth - left;
            }


            float speedFactor = (speeds.get(i) - minSpeed) / (maxSpeed - minSpeed);
            speedFactor = Math.max(0f, Math.min(speedFactor, 1f)); // clamp 0..1


            float barHeight = viewHeight * speedFactor;


            // Enforce minimal height so slowest lap is visible
            if (barHeight < MIN_BAR_HEIGHT_PX) {
                barHeight = MIN_BAR_HEIGHT_PX;
            }


            float top = viewHeight - barHeight;


            int color = interpolateColor(Color.BLUE, Color.RED, speedFactor);
            paint.setColor(color);


            canvas.drawRect(left, top, left + lapWidth, viewHeight, paint);


            Log.d(TAG, String.format("Bar %d: left=%.2f, width=%.2f, height=%.2f, color=%#08X",
                    i, left, lapWidth, barHeight, color));


            left += lapWidth;
        }
    }


    private int interpolateColor(int colorStart, int colorEnd, float factor) {
        int alpha = (int) (Color.alpha(colorStart) + factor * (Color.alpha(colorEnd) - Color.alpha(colorStart)));
        int red = (int) (Color.red(colorStart) + factor * (Color.red(colorEnd) - Color.red(colorStart)));
        int green = (int) (Color.green(colorStart) + factor * (Color.green(colorEnd) - Color.green(colorStart)));
        int blue = (int) (Color.blue(colorStart) + factor * (Color.blue(colorEnd) - Color.blue(colorStart)));
        return Color.argb(alpha, red, green, blue);
    }
}


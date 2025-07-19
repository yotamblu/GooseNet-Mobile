package com.example.goosenetmobile.classes;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.example.goosenetmobile.R;

public class WorkoutMarkerView extends MarkerView {
    private final TextView markerText;
    private final String type;

    public WorkoutMarkerView(Context context, int layoutResource, String type) {
        super(context, layoutResource);
        this.markerText = findViewById(R.id.marker_text);
        this.type = type; // "heart", "elevation", or "speed"
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int timeSec = (int) e.getX();
        int min = timeSec / 60;
        int sec = timeSec % 60;

        float value = e.getY();
        String formatted = "";

        switch (type) {
            case "heart":
                formatted = String.format("HR: %.0f bpm", value);
                break;
            case "elevation":
                formatted = String.format("Elevation: %.0f m", value);
                break;
            case "speed":
                if (value > 0) {
                    float pace = (float) (1000.0 / 60.0 / value); // min/km
                    int paceMin = (int) pace;
                    int paceSec = Math.round((pace - paceMin) * 60);
                    formatted = String.format("Pace: %d:%02d /km", paceMin, paceSec);
                } else {
                    formatted = "Pace: --:-- /km";
                }
                break;
        }

        markerText.setText(String.format("Time: %02d:%02d\n%s", min, sec, formatted));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}

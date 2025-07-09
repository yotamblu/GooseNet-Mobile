package com.example.goosenetmobile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
public class AddWorkoutActivity extends AppCompatActivity {

    private EditText nameEditText, descEditText, dateEditText;
    private LinearLayout intervalsContainer;
    private Button addIntervalButton, saveButton;
    private TextView addWorkoutTitle;
    private Dialog progressDialog;
    private boolean isFlock = false;

    private final String[] durationUnits = {"Seconds", "Minutes", "Meters", "Kilometers"};
    private final String[] stepTypes = {"Run", "Rest"};
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> dateEditText.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        nameEditText = findViewById(R.id.workoutName);
        descEditText = findViewById(R.id.workoutDesc);
        dateEditText = findViewById(R.id.workoutDate);
        intervalsContainer = findViewById(R.id.intervalsContainer);
        addIntervalButton = findViewById(R.id.addIntervalButton);
        saveButton = findViewById(R.id.saveWorkoutButton);
        addWorkoutTitle = findViewById(R.id.addWorkoutTitle);
        final String[] athleteName = {""};
        final String[] flockName = {""};
        Bundle intentData = getIntent().getExtras();
        if (intentData != null && intentData.containsKey("athleteName")) {
             athleteName[0] = intentData.getString("athleteName");
            addWorkoutTitle.setText("Add Workout for @" + athleteName[0]);
        } else if (intentData.containsKey("flockName")) {
            isFlock = true;
            flockName[0]  =intentData.getString("flockName");
            addWorkoutTitle.setText("Add Workout for the flock " + flockName[0]);
        }

        dateEditText.setInputType(InputType.TYPE_NULL);
        dateEditText.setOnClickListener(v -> showDatePicker());

        addIntervalButton.setOnClickListener(v -> intervalsContainer.addView(createIntervalView()));

        saveButton.setOnClickListener(v -> {
            if (!validateInputs()) {
                Toast.makeText(this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject json = generateGarminJson();
                Log.d("WorkoutJSON", json.toString(2));
                showBlockingProgressDialog();
                new Thread( () ->{
                    ApiService.addWorkout(json.toString(),dateEditText.getText().toString(), !athleteName[0].equals("") ? athleteName[0] : flockName[0],isFlock,AddWorkoutActivity.this);
                    runOnUiThread(() ->{
                        hideBlockingProgressDialog();
                        finish();
                    });


                }).start();
                Toast.makeText(this, "Workout JSON pushed to GARMIN", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error with workout data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View createIntervalView() {
        LinearLayout intervalLayout = new LinearLayout(this);
        intervalLayout.setOrientation(LinearLayout.VERTICAL);
        intervalLayout.setPadding(16, 16, 16, 16);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("Interval");
        title.setTypeface(null, Typeface.BOLD);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        Button deleteBtn = new Button(this);
        deleteBtn.setText("Delete");
        deleteBtn.setOnClickListener(v -> intervalsContainer.removeView(intervalLayout));
        header.addView(title);
        header.addView(deleteBtn);

        EditText repeatInput = new EditText(this);
        repeatInput.setHint("Repeat Count");
        repeatInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        intervalLayout.setTag(R.id.repeat_input, repeatInput);

        LinearLayout subContainer = new LinearLayout(this);
        subContainer.setOrientation(LinearLayout.VERTICAL);
        int subId = View.generateViewId();
        subContainer.setId(subId);
        intervalLayout.setTag(subId);

        Button addSubBtn = new Button(this);
        addSubBtn.setText("Add Sub-Interval");
        addSubBtn.setOnClickListener(v -> subContainer.addView(createSubIntervalView()));

        intervalLayout.addView(header);
        intervalLayout.addView(repeatInput);
        intervalLayout.addView(subContainer);
        intervalLayout.addView(addSubBtn);

        subContainer.addView(createSubIntervalView());

        return intervalLayout;
    }

    private View createSubIntervalView() {
        LinearLayout subLayout = new LinearLayout(this);
        subLayout.setOrientation(LinearLayout.VERTICAL);
        subLayout.setPadding(8, 8, 8, 8);

        EditText paceMin = new EditText(this);
        paceMin.setHint("Pace Min");
        paceMin.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText paceSec = new EditText(this);
        paceSec.setHint("Pace Sec");
        paceSec.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText durationInput = new EditText(this);
        durationInput.setHint("Duration Value");
        durationInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        Spinner durationSpinner = new Spinner(this);
        durationSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, durationUnits));

        Spinner typeSpinner = new Spinner(this);
        typeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stepTypes));

        Button removeBtn = new Button(this);
        removeBtn.setText("Remove");
        removeBtn.setOnClickListener(v -> ((ViewGroup) subLayout.getParent()).removeView(subLayout));

        subLayout.addView(paceMin);
        subLayout.addView(paceSec);
        subLayout.addView(durationInput);
        subLayout.addView(durationSpinner);
        subLayout.addView(typeSpinner);
        subLayout.addView(removeBtn);

        return subLayout;
    }

    private boolean validateInputs() {
        if (nameEditText.getText().toString().isEmpty()) return false;
        if (descEditText.getText().toString().isEmpty()) return false;
        if (dateEditText.getText().toString().isEmpty()) return false;
        if (intervalsContainer.getChildCount() == 0) return false;

        for (int i = 0; i < intervalsContainer.getChildCount(); i++) {
            View intervalView = intervalsContainer.getChildAt(i);
            EditText repeatInput = (EditText) intervalView.getTag(R.id.repeat_input);
            if (repeatInput.getText().toString().isEmpty()) return false;

            int subId = (int) intervalView.getTag();
            LinearLayout subContainer = intervalView.findViewById(subId);
            if (subContainer.getChildCount() == 0) return false;

            for (int j = 0; j < subContainer.getChildCount(); j++) {
                LinearLayout sub = (LinearLayout) subContainer.getChildAt(j);
                for (int k = 0; k < 3; k++) {
                    View v = sub.getChildAt(k);
                    if (v instanceof EditText && ((EditText) v).getText().toString().isEmpty()) return false;
                }
            }
        }

        return true;
    }

    private void showBlockingProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View view = LayoutInflater.from(this).inflate(R.layout.add_workout_progress_loader, null);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false); // Block back button
            progressDialog.setContentView(view);
        }
        progressDialog.show();
    }

    private void hideBlockingProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private JSONObject generateGarminJson() throws Exception {
        JSONObject root = new JSONObject();
        root.put("sport", "RUNNING");
        root.put("workoutName", nameEditText.getText().toString());
        root.put("description", descEditText.getText().toString());

        JSONArray steps = new JSONArray();

        for (int i = 0; i < intervalsContainer.getChildCount(); i++) {
            View intervalView = intervalsContainer.getChildAt(i);
            EditText repeatInput = (EditText) intervalView.getTag(R.id.repeat_input);
            int repeatCount = Integer.parseInt(repeatInput.getText().toString());

            int subId = (int) intervalView.getTag();
            LinearLayout subContainer = intervalView.findViewById(subId);

            JSONArray subSteps = new JSONArray();

            for (int j = 0; j < subContainer.getChildCount(); j++) {
                LinearLayout sub = (LinearLayout) subContainer.getChildAt(j);
                int min = Integer.parseInt(((EditText) sub.getChildAt(0)).getText().toString());
                int sec = Integer.parseInt(((EditText) sub.getChildAt(1)).getText().toString());
                double totalMin = min + (sec / 60.0);
                double mps = 1000.0 / (totalMin * 60.0);

                double dur = Double.parseDouble(((EditText) sub.getChildAt(2)).getText().toString());
                String durUnit = ((Spinner) sub.getChildAt(3)).getSelectedItem().toString();
                String type = ((Spinner) sub.getChildAt(4)).getSelectedItem().toString();
                String intensity = type.equalsIgnoreCase("Rest") ? "REST" : "INTERVAL";

                String durationType = durUnit.equals("Seconds") || durUnit.equals("Minutes") ? "TIME" : "DISTANCE";
                if (durUnit.equals("Minutes")) dur *= 60;
                if (durUnit.equals("Kilometers")) dur *= 1000;

                JSONObject subJson = new JSONObject();
                subJson.put("targetType", "PACE");
                subJson.put("stepOrder", j + 1);
                subJson.put("repeatValue", 0);
                subJson.put("type", "WorkoutStep");
                subJson.put("steps", JSONObject.NULL);
                subJson.put("description", type);
                subJson.put("durationType", durationType);
                subJson.put("durationValue", dur);
                subJson.put("intensity", intensity);
                subJson.put("targetValueLow", mps);
                subJson.put("targetValueHigh", mps);
                subJson.put("repeatType", JSONObject.NULL);

                subSteps.put(subJson);
            }

            JSONObject interval = new JSONObject();
            interval.put("targetType", "PACE");
            interval.put("stepOrder", i + 1);
            interval.put("repeatValue", repeatCount);
            interval.put("type", "WorkoutRepeatStep");
            interval.put("steps", subSteps);
            interval.put("description", "Interval " + (i + 1));
            interval.put("durationType", JSONObject.NULL);
            interval.put("durationValue", 0.0);
            interval.put("intensity", "INTERVAL");
            interval.put("targetValueLow", 0.0);
            interval.put("targetValueHigh", 0.0);
            interval.put("repeatType", "REPEAT_UNTIL_STEPS_CMPLT");

            steps.put(interval);
        }

        root.put("steps", steps);
        return root;
    }
}



package com.example.goosenetmobile;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goosenetmobile.classes.PlannedWorkout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PlannedWorkoutsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextInputEditText dateEditText;
    private ListView workoutListView;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    String athleteName;
    private TextView title;
    private LinearLayout progressOverlay;

    private String mParam1;
    private String mParam2;

    public PlannedWorkoutsFragment() {
    }

    public static PlannedWorkoutsFragment newInstance(String param1, String param2) {
        PlannedWorkoutsFragment fragment = new PlannedWorkoutsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static String convertToNumericFormat(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return inputDate;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planned_workouts, container, false);
        dateEditText = view.findViewById(R.id.dateEditText);
        workoutListView = view.findViewById(R.id.plannedWorkoutListView);
        progressOverlay = view.findViewById(R.id.progressOverlay);
        title = view.findViewById(R.id.titleTextView);
        dateEditText.setText("Select A date");
        dateEditText.setOnClickListener(v -> showDatePicker());

        if (((Activity) requireContext()) instanceof MainPageActivity) {
            athleteName = PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .getString(GooseNetUtil.IS_LOGGEDIN_KEY, "");
            title.setText("View your planned workouts");
        } else {
            athleteName = requireActivity().getIntent().getStringExtra("athleteName");
            title.setText("View Planned Workouts For @" + athleteName);
        }
        return view;
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Workout Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            progressOverlay.setVisibility(View.VISIBLE);
            workoutListView.setVisibility(View.GONE);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            String formattedDate = dateFormat.format(calendar.getTime());
            dateEditText.setText(formattedDate);

            new Thread(() -> {
                try {
                    if (!isAdded()) return;
                    List<PlannedWorkout> summaryList = ApiService.getPlannedWorkoutsByDate(
                            athleteName, convertToNumericFormat(formattedDate), requireContext());
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;
                        if (summaryList == null || summaryList.isEmpty()) {
                            Toast.makeText(requireContext(), "No planned workouts found for this date", Toast.LENGTH_SHORT).show();
                            workoutListView.setAdapter(null);
                        } else {
                            PlannedWorkoutAdapter adapter = new PlannedWorkoutAdapter(requireContext(), summaryList);
                            workoutListView.setAdapter(adapter);
                        }
                        progressOverlay.setVisibility(View.GONE);
                        workoutListView.setVisibility(View.VISIBLE);
                    });
                } catch (Exception e) {
                    Log.e("PlannedWorkoutsFragment", "Failed to load planned workouts", e);
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            if (!isAdded()) return;
                            progressOverlay.setVisibility(View.GONE);
                            workoutListView.setVisibility(View.VISIBLE);
                            Toast.makeText(requireContext(), "Failed to load planned workouts", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }).start();
        });
    }
}

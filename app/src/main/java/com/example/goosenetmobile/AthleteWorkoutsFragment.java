package com.example.goosenetmobile;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.goosenetmobile.classes.WorkoutSummary;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AthleteWorkoutsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1"; // e.g., athlete name
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextInputEditText dateEditText;
    private ListView workoutsListView;
    private String athleteName;
    private LinearLayout progressOverlay;
    TextView title;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public AthleteWorkoutsFragment() {
        // Required empty public constructor
    }

    public static AthleteWorkoutsFragment newInstance(String param1, String param2) {
        AthleteWorkoutsFragment fragment = new AthleteWorkoutsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1); // param1 can be athleteName
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_athlete_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateEditText = view.findViewById(R.id.dateEditText);
        workoutsListView = view.findViewById(R.id.workoutSummaryListView);
        progressOverlay = view.findViewById(R.id.progressOverlay);
        title = requireActivity().findViewById(R.id.titleTextView);

        dateEditText.setOnClickListener(v -> showDatePicker());

        // Example: Set initial date (optional)
        dateEditText.setText("Select A date");
        if(((Activity)requireContext()) instanceof MainPageActivity){
            athleteName = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(GooseNetUtil.IS_LOGGEDIN_KEY,"");
            title.setText("View your completed workouts");
        }else{
            athleteName = requireActivity().getIntent().getStringExtra("athleteName");
            title.setText("View Completed Workouts By @" + athleteName);
        }

    }

    public static String convertToNumericFormat(String inputDate) {
        try {
            // From: "Jul 10, 2025"
            SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
            // To: "7/10/2025"
            SimpleDateFormat outputFormat = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);

            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate; // fallback to original if parsing fails
        }
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Workout Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            progressOverlay.setVisibility(View.VISIBLE);
            workoutsListView.setVisibility(View.GONE);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            String formattedDate = dateFormat.format(calendar.getTime());
            dateEditText.setText(formattedDate);

            new Thread(() ->{
                List<WorkoutSummary> summaryList = ApiService.getWorkoutSummaries(requireContext(),convertToNumericFormat(formattedDate),athleteName);

                requireActivity().runOnUiThread(() -> {
                    if (summaryList != null) {
                        WorkoutSummaryAdapter adapter = new WorkoutSummaryAdapter(requireContext(), summaryList);
                        workoutsListView.setAdapter(adapter);
                    }

                    progressOverlay.setVisibility(View.GONE);
                    workoutsListView.setVisibility(View.VISIBLE);

                });})

                    .start();
        });
    }
}

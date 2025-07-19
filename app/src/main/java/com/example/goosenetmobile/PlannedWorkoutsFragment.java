package com.example.goosenetmobile;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.goosenetmobile.classes.PlannedWorkout;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlannedWorkoutsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlannedWorkoutsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextInputEditText dateEditText;
    private ListView workoutListView;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    String athleteName;
    private TextView title;
    private LinearLayout progressOverlay;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlannedWorkoutsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlannedWorkoutsFragment.
     */
    // TODO: Rename and change types and number of parameters
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Example: Set initial date (optional)
        View view =inflater.inflate(R.layout.fragment_planned_workouts, container, false);
        dateEditText = view.findViewById(R.id.dateEditText);
        workoutListView = view.findViewById(R.id.plannedWorkoutListView);
        progressOverlay = view.findViewById(R.id.progressOverlay);
        title = view.findViewById(R.id.titleTextView);
        dateEditText.setText("Select A date");
        dateEditText.setOnClickListener(v -> showDatePicker());

        if(((Activity)requireContext()) instanceof MainPageActivity){
            athleteName = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(GooseNetUtil.IS_LOGGEDIN_KEY,"");
            title.setText("View your completed workouts");

        }else{
            athleteName = requireActivity().getIntent().getStringExtra("athleteName");
            title.setText("View Completed Workouts By @" + athleteName);
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

            new Thread(() ->{
                List<PlannedWorkout> summaryList = ApiService.getPlannedWorkoutsByDate(athleteName,convertToNumericFormat(formattedDate),requireContext());

                requireActivity().runOnUiThread(() -> {
                    if (summaryList != null) {
                        PlannedWorkoutAdapter adapter = new PlannedWorkoutAdapter(requireContext(), summaryList);
                        workoutListView.setAdapter(adapter);
                    }

                    progressOverlay.setVisibility(View.GONE);
                    workoutListView.setVisibility(View.VISIBLE);

                });})

                    .start();
        });
    }

}
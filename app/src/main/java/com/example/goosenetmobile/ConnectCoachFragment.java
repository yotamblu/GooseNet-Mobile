package com.example.goosenetmobile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectCoachFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button connectCoachButton;
    private EditText coachIdInput;
    private String mParam1;
    private String mParam2;
    public static final int CONNECT_COACH_REQUEST_CODE = 222;

    public ConnectCoachFragment() {
    }

    public static ConnectCoachFragment newInstance(String param1, String param2) {
        ConnectCoachFragment fragment = new ConnectCoachFragment();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!isAdded()) return;
        if (requestCode == CONNECT_COACH_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(requireContext(), "Coach connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "You cannot connect with the same Coach Twice", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        connectCoachButton = view.findViewById(R.id.btnSubmitCoachId);
        coachIdInput = view.findViewById(R.id.coachIdEditText);

        connectCoachButton.setOnClickListener(v -> {
            String suppliedCoachId = coachIdInput.getText().toString();

            new Thread(() -> {
                try {
                    if (!isAdded()) return;
                    if (suppliedCoachId.isEmpty()) {
                        requireActivity().runOnUiThread(() -> {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Please enter a coach ID", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }

                    String coachName = ApiService.getCoachName(suppliedCoachId);
                    if (!isAdded()) return;

                    if (coachName == null || coachName.isEmpty()) {
                        requireActivity().runOnUiThread(() -> {
                            if (!isAdded()) return;
                            coachIdInput.setText("");
                            Toast.makeText(requireContext(), "Coach ID not found", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            if (!isAdded()) return;
                            coachIdInput.setText("");
                            Intent confirmationIntent = new Intent(requireContext(), CoachConnectionConfirmationActivity.class);
                            confirmationIntent.putExtra("coachName", coachName);
                            confirmationIntent.putExtra("coachId", suppliedCoachId);
                            startActivityForResult(confirmationIntent, CONNECT_COACH_REQUEST_CODE);
                        });
                    }
                } catch (Exception e) {
                    Log.e("ConnectCoachFragment", "Failed to connect coach", e);
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Failed to connect to coach", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }).start();
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect_coach, container, false);
    }
}

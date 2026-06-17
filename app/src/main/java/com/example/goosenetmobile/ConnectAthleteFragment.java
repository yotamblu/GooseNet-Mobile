package com.example.goosenetmobile;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectAthleteFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String coachId;
    private TextView coachIdTV;
    private Button copyButton, whatsappButton;

    private String mParam1;
    private String mParam2;

    public ConnectAthleteFragment() {
    }

    public static ConnectAthleteFragment newInstance(String param1, String param2) {
        ConnectAthleteFragment fragment = new ConnectAthleteFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        coachIdTV = view.findViewById(R.id.coachIdText);
        whatsappButton = view.findViewById(R.id.btnShareOnWhatsApp);
        copyButton = view.findViewById(R.id.btnCopyCoachId);

        new Thread(() -> {
            try {
                if (!isAdded()) return;
                coachId = ApiService.getCoachId(
                        PreferenceManager.getDefaultSharedPreferences(requireContext())
                                .getString("loggedInUserName", ""));
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    coachIdTV.setText("Your Coach ID:" + coachId);

                    copyButton.setOnClickListener(v -> {
                        try {
                            ClipboardManager clipboard = (ClipboardManager)
                                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Coach ID", coachId);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(requireContext(), "Coach ID copied", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("ConnectAthleteFragment", "Failed to copy coach ID", e);
                        }
                    });

                    whatsappButton.setOnClickListener(v -> {
                        try {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT,
                                    "Join me on GooseNet! My coach ID is: " + coachId);
                            sendIntent.setType("text/plain");
                            sendIntent.setPackage("com.whatsapp");
                            startActivity(sendIntent);
                        } catch (ActivityNotFoundException e) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("ConnectAthleteFragment", "WhatsApp share failed", e);
                        }
                    });
                });
            } catch (Exception e) {
                Log.e("ConnectAthleteFragment", "Failed to load coach ID", e);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Failed to load coach ID", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect_athlete, container, false);
    }
}

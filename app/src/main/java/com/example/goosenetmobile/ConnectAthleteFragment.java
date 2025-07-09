package com.example.goosenetmobile;

import static androidx.core.content.ContextCompat.getSystemService;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConnectAthleteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectAthleteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String coachId;
    private TextView coachIdTV;
    private Button copyButton,whatsappButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConnectAthleteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConnectAthleteFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        new Thread(() ->{
             coachId = ApiService.getCoachId(PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("loggedInUserName",""));
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Coach ID", coachId);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(requireContext(), "Coach ID copied", Toast.LENGTH_SHORT).show();
                }
            });

            whatsappButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Join me on GooseNet! My coach ID is: " + coachId);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");

                    try {
                        startActivity(sendIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
             requireActivity().runOnUiThread(() -> {
                 coachIdTV.setText("Your Coach ID:" + coachId);
             });

        }).start();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_athlete, container, false);
    }
}
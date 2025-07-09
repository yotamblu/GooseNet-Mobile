package com.example.goosenetmobile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConnectCoachFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConnectCoachFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Button connectCoachButton;
    private EditText coachIdInput;
    private String mParam1;
    private String mParam2;
    public static final int CONNECT_COACH_REQUEST_CODE = 222;

    public ConnectCoachFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConnectCoachFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if(requestCode == CONNECT_COACH_REQUEST_CODE && resultCode == RESULT_OK){
          Toast.makeText(requireContext(),"Coach connected",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(requireContext(), "You cannot connect with the same Coach Twice", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        connectCoachButton = view.findViewById(R.id.btnSubmitCoachId);
        coachIdInput = view.findViewById(R.id.coachIdEditText);
        connectCoachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String suppliedCoachId = coachIdInput.getText().toString();

                new Thread(() -> {
                    if (suppliedCoachId.isEmpty()) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Please enter a coach ID", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        String coachName = ApiService.getCoachName(suppliedCoachId);
                        if(coachName.isEmpty()){
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Coach ID not found", Toast.LENGTH_SHORT).show()
                            );
                        }else{
                            requireActivity().runOnUiThread(() ->{
                                        coachIdInput.setText("");
                                        Intent confirmationIntent = new Intent(requireContext(),CoachConnectionConfirmationActivity.class);
                                        confirmationIntent.putExtra("coachName",coachName);
                                        confirmationIntent.putExtra("coachId",suppliedCoachId);
                                        startActivityForResult(confirmationIntent,CONNECT_COACH_REQUEST_CODE);
                                    }
                            );
                        }
                       requireActivity().runOnUiThread(() -> coachIdInput.setText(""));
                    }
                    requireActivity().runOnUiThread(() ->coachIdInput.setText(""));

                }).start();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_coach, container, false);
    }
}
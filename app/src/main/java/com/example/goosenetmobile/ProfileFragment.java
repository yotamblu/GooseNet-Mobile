package com.example.goosenetmobile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    public static final int CONNECTED_TO_GARMIN = 111;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView profileImage;
    private TextView  usernameText;
    private Button editProfileButton, logoutButton, connectGarminButton;

    // Optional parameters (if you want to keep)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private boolean shouldSeeConnectButton(){
        return ApiService.getRole(GooseNetUtil.getApiKey(requireContext())).equals("athlete") && !ApiService.isConnectedToGarmin(GooseNetUtil.getApiKey(requireContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONNECTED_TO_GARMIN  && resultCode == RESULT_OK){
            refreshProfileData();
        }
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        profileImage = view.findViewById(R.id.profile_image);
        Glide.with(this)
                .asGif()
                .load(R.drawable.loading)
                .circleCrop()
                .into(profileImage);

        usernameText = view.findViewById(R.id.usernameText);
        usernameText.setText("@" + PreferenceManager
                .getDefaultSharedPreferences(requireContext()).getString("loggedInUserName",""));
        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        connectGarminButton = view.findViewById(R.id.connectGarminButton);
        // Setup swipe to refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh action: runs in background thread
               refreshProfileData();
        });


        //define logout functionality
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Logout clicked", Toast.LENGTH_SHORT).show();

            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().clear().apply();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                 requireActivity().finish();
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), EditProfileMenuActivity.class));
            }
        });

        connectGarminButton.setOnClickListener(v ->{
            startActivityForResult(new Intent(requireContext(), ConnectToGarminActivity.class),CONNECTED_TO_GARMIN);
        });
        //check if user should have connect to garmin button
        new Thread(() ->{
            if(shouldSeeConnectButton()){
                new Handler(Looper.getMainLooper()).post(() -> {
                    connectGarminButton.setVisibility(View.VISIBLE);
                });
            }else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    connectGarminButton.setVisibility(View.GONE);
                });
            }
        }).start();


        // Optionally trigger initial load here or elsewhere
        // Show refresh animation
        swipeRefreshLayout.setRefreshing(true);

        // Manually trigger your refresh logic method
        refreshProfileData();



    }


    private void refreshProfileData() {
        new Thread(() -> {

            Bitmap profilePic = ApiService.getProfilePicBitmap(PreferenceManager
                    .getDefaultSharedPreferences(requireContext()).getString("loggedInUserName",""));

            if(shouldSeeConnectButton()){
                new Handler(Looper.getMainLooper()).post(() -> {
                    connectGarminButton.setVisibility(View.VISIBLE);
                });
            }else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    connectGarminButton.setVisibility(View.GONE);
                });
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    Glide.with(requireContext())
                            .load(profilePic) // this can be a Bitmap, Uri, or File
                            .circleCrop()
                            .into(profileImage);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }

                profileImage.setBackgroundResource(R.drawable.circle_background);

                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }


    // Simulated network calls (replace with your real logic)



}

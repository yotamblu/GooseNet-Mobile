package com.example.goosenetmobile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment {
    public static final int CONNECTED_TO_GARMIN = 111;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView profileImage;
    private TextView usernameText;
    private Button editProfileButton, logoutButton, connectGarminButton;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    private boolean shouldSeeConnectButton() {
        try {
            if (!isAdded()) return false;
            String role = ApiService.getRole(GooseNetUtil.getApiKey(requireContext()));
            boolean connected = ApiService.isConnectedToGarmin(GooseNetUtil.getApiKey(requireContext()));
            return "athlete".equals(role) && !connected;
        } catch (Exception e) {
            Log.e("ProfileFragment", "shouldSeeConnectButton failed", e);
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONNECTED_TO_GARMIN && resultCode == RESULT_OK) {
            refreshProfileData();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        profileImage = view.findViewById(R.id.profile_image);
        Glide.with(this)
                .asGif()
                .load(R.drawable.loading)
                .circleCrop()
                .into(profileImage);

        usernameText = view.findViewById(R.id.usernameText);
        usernameText.setText("@" + PreferenceManager
                .getDefaultSharedPreferences(requireContext()).getString("loggedInUserName", ""));
        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        connectGarminButton = view.findViewById(R.id.connectGarminButton);

        swipeRefreshLayout.setOnRefreshListener(this::refreshProfileData);

        logoutButton.setOnClickListener(v -> {
            try {
                Toast.makeText(requireContext(), "Logout clicked", Toast.LENGTH_SHORT).show();
                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().clear().apply();
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            } catch (Exception e) {
                Log.e("ProfileFragment", "Logout failed", e);
            }
        });

        editProfileButton.setOnClickListener(v -> {
            try {
                startActivity(new Intent(requireContext(), EditProfileMenuActivity.class));
            } catch (Exception e) {
                Log.e("ProfileFragment", "Open edit profile failed", e);
            }
        });

        connectGarminButton.setOnClickListener(v -> {
            try {
                startActivityForResult(new Intent(requireContext(), ConnectToGarminActivity.class), CONNECTED_TO_GARMIN);
            } catch (Exception e) {
                Log.e("ProfileFragment", "Open Garmin connect failed", e);
            }
        });

        new Thread(() -> {
            try {
                if (!isAdded()) return;
                boolean show = shouldSeeConnectButton();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    connectGarminButton.setVisibility(show ? View.VISIBLE : View.GONE);
                });
            } catch (Exception e) {
                Log.e("ProfileFragment", "Garmin button visibility check failed", e);
            }
        }).start();

        swipeRefreshLayout.setRefreshing(true);
        refreshProfileData();
    }

    private void refreshProfileData() {
        new Thread(() -> {
            try {
                if (!isAdded()) return;
                String userName = PreferenceManager
                        .getDefaultSharedPreferences(requireContext()).getString("loggedInUserName", "");
                String profilePicRaw = ApiService.getProfilePicRaw(userName);

                boolean show = shouldSeeConnectButton();
                if (!isAdded()) return;
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    connectGarminButton.setVisibility(show ? View.VISIBLE : View.GONE);
                });

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!isAdded()) return;
                    try {
                        Bitmap profilePic = GooseNetUtil.base64ToBitmap(profilePicRaw);
                        if (profilePic != null) {
                            Glide.with(requireContext())
                                    .load(profilePic)
                                    .circleCrop()
                                    .into(profileImage);
                        } else {
                            Glide.with(requireContext())
                                    .asGif()
                                    .load(R.drawable.loading)
                                    .circleCrop()
                                    .into(profileImage);
                        }
                    } catch (Exception ex) {
                        if (!isAdded()) return;
                        try {
                            Glide.with(requireContext())
                                    .load(profilePicRaw)
                                    .circleCrop()
                                    .into(profileImage);
                        } catch (Exception inner) {
                            Log.e("ProfileFragment", "Failed to load profile picture", inner);
                        }
                    }
                    profileImage.setBackgroundResource(R.drawable.circle_background);
                    swipeRefreshLayout.setRefreshing(false);
                });
            } catch (Exception e) {
                Log.e("ProfileFragment", "refreshProfileData failed", e);
                if (isAdded()) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (!isAdded()) return;
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        }).start();
    }
}

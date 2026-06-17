package com.example.goosenetmobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.goosenetmobile.classes.AthleteCard;

import java.util.ArrayList;
import java.util.List;

public class MyAthletesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GridView athletesView;
    private SwipeRefreshLayout refreshLayout;
    LinearLayout progressOverLay;
    private String mParam1;
    private String mParam2;

    public MyAthletesFragment() {
    }

    public static MyAthletesFragment newInstance(String param1, String param2) {
        MyAthletesFragment fragment = new MyAthletesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void RefreshAthletes() {
        progressOverLay.setVisibility(View.VISIBLE);
        athletesView.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                if (!isAdded()) return;
                List<AthleteCard> athletesData = ApiService.getAthletesData(requireContext());
                if (!isAdded()) return;
                List<AthleteCard> safeData = (athletesData != null) ? athletesData : new ArrayList<>();
                AthleteCardAdapter adapter = new AthleteCardAdapter(requireContext(), safeData);
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    if (safeData.isEmpty()) {
                        Toast.makeText(requireContext(), "No athletes found", Toast.LENGTH_SHORT).show();
                    }
                    athletesView.setAdapter(adapter);
                    athletesView.setVisibility(View.VISIBLE);
                    progressOverLay.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                Log.e("MyAthletesFragment", "Failed to load athletes", e);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;
                        progressOverLay.setVisibility(View.GONE);
                        athletesView.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), "Failed to load athletes", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
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
        athletesView = view.findViewById(R.id.athleteGridView);
        progressOverLay = view.findViewById(R.id.progressOverlay);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(() -> {
            RefreshAthletes();
            refreshLayout.setRefreshing(false);
        });
        RefreshAthletes();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_athletes, container, false);
    }
}

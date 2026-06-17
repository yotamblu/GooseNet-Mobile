package com.example.goosenetmobile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.goosenetmobile.classes.FlockCard;

import java.util.ArrayList;
import java.util.List;

public class FlocksFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout refreshLayout;
    private Button createFlockButton;
    LinearLayout progressOverLay;
    private ListView flockCardsView;
    private final int CREATE_FLOCK_REQUEST = 250;

    private String mParam1;
    private String mParam2;

    public FlocksFragment() {
    }

    public static FlocksFragment newInstance(String param1, String param2) {
        FlocksFragment fragment = new FlocksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void RefreshFlocks() {
        progressOverLay.setVisibility(View.VISIBLE);
        flockCardsView.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                if (!isAdded()) return;
                List<String> flockNames = ApiService.getFlockNames(requireContext());
                List<FlockCard> flockCards = new ArrayList<>();
                if (flockNames != null) {
                    for (String flockName : flockNames) {
                        flockCards.add(new FlockCard(flockName));
                    }
                }
                if (!isAdded()) return;
                FlockCardAdapter adapter = new FlockCardAdapter(requireContext(), flockCards);
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    if (flockNames == null || flockNames.isEmpty()) {
                        Toast.makeText(requireContext(), "No flocks found", Toast.LENGTH_SHORT).show();
                    }
                    flockCardsView.setAdapter(adapter);
                    flockCardsView.setVisibility(View.VISIBLE);
                    progressOverLay.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                Log.e("FlocksFragment", "Failed to load flocks", e);
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;
                        progressOverLay.setVisibility(View.GONE);
                        flockCardsView.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), "Failed to load flocks", Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FLOCK_REQUEST && resultCode == RESULT_OK) {
            RefreshFlocks();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flocks, container, false);
        progressOverLay = view.findViewById(R.id.progressOverlay);
        flockCardsView = view.findViewById(R.id.flocksListView);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        createFlockButton = view.findViewById(R.id.createFlockButton);
        createFlockButton.setOnClickListener(v ->
                requireActivity().startActivityForResult(
                        new Intent(requireContext(), CreateFlockActivity.class), CREATE_FLOCK_REQUEST));
        refreshLayout.setOnRefreshListener(() -> {
            RefreshFlocks();
            refreshLayout.setRefreshing(false);
        });
        RefreshFlocks();
        return view;
    }
}

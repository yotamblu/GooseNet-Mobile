package com.example.goosenetmobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.goosenetmobile.classes.AthleteCard;
import com.example.goosenetmobile.classes.GridSpacingItemDecoration;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAthletesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAthletesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private GridView athletesView;
    private SwipeRefreshLayout refreshLayout;
    LinearLayout progressOverLay;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyAthletesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAthletesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAthletesFragment newInstance(String param1, String param2) {
        MyAthletesFragment fragment = new MyAthletesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void RefreshAthletes(){
        progressOverLay.setVisibility(View.VISIBLE);
        athletesView.setVisibility(View.GONE);

        new Thread(() ->{
            try{
                List<AthleteCard> athletesData = ApiService.getAthletesData(requireContext());
                AthleteCardAdapter adapter = new AthleteCardAdapter(requireContext(),athletesData);
                requireActivity().runOnUiThread(() -> {
                    athletesView.setAdapter(adapter);
                    athletesView.setVisibility(View.VISIBLE);
                    progressOverLay.setVisibility(View.GONE);
                    athletesView.setVisibility(View.VISIBLE);
                });
            } catch (RuntimeException ignored) {

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
        progressOverLay  = view.findViewById(R.id.progressOverlay);

        refreshLayout = view.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshAthletes();
                refreshLayout.setRefreshing(false);

            }
        });
        RefreshAthletes();

        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_athletes, container, false);
    }
}
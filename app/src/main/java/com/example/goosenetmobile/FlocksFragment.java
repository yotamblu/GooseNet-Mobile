package com.example.goosenetmobile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.goosenetmobile.classes.FlockCard;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FlocksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlocksFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SwipeRefreshLayout refreshLayout;
    private Button createFlockButton;
    LinearLayout progressOverLay;
    private ListView flockCardsView;
    private final int CREATE_FLOCK_REQUEST = 250;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FlocksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FlocksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FlocksFragment newInstance(String param1, String param2) {
        FlocksFragment fragment = new FlocksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private void RefreshFlocks(){
        progressOverLay.setVisibility(View.VISIBLE);
        flockCardsView.setVisibility(View.GONE);

        new Thread(() ->{
            try{
                List<String> flockNames = ApiService.getFlockNames(requireContext());
                List<FlockCard> flockCards = new ArrayList<>();
                for (String flockName:
                     flockNames) {
                    flockCards.add(new FlockCard(flockName));
                }
                FlockCardAdapter adapter = new FlockCardAdapter(requireContext(),flockCards);
                requireActivity().runOnUiThread(() -> {
                    flockCardsView.setAdapter(adapter);
                    flockCardsView.setVisibility(View.VISIBLE);
                    progressOverLay.setVisibility(View.GONE);
                    flockCardsView.setVisibility(View.VISIBLE);
                });
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
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
        if(requestCode == CREATE_FLOCK_REQUEST && resultCode == RESULT_OK){
            RefreshFlocks();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_flocks, container, false);
        // Inflate the layout for this fragment
        progressOverLay = view.findViewById(R.id.progressOverlay);
        flockCardsView = view.findViewById(R.id.flocksListView);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        createFlockButton = view.findViewById(R.id.createFlockButton);
        createFlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().startActivityForResult(new Intent(requireContext(),CreateFlockActivity.class),CREATE_FLOCK_REQUEST);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshFlocks();
                refreshLayout.setRefreshing(false);

            }
        });
        RefreshFlocks();
        return view;

    }
}
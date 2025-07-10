package com.example.goosenetmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainPageActivity extends AppCompatActivity {

    BottomNavigationView bottomNavBar;
    private ProfileFragment profileFragment;
    private ConnectAthleteFragment  connectToAthleteFragment;
    private ConnectCoachFragment connectCoachFragment;
    private MyAthletesFragment myAthletesFragment;
    private FlocksFragment flocksFragment;
    private AthleteWorkoutsFragment athleteWorkoutsFragment;
    private void initializeObjects() {
        bottomNavBar = findViewById(R.id.bottom_navigation);
    }

    private void InitializeFragments(){

        profileFragment = new ProfileFragment();
        connectToAthleteFragment = new ConnectAthleteFragment();
        connectCoachFragment = new ConnectCoachFragment();
        myAthletesFragment = new MyAthletesFragment();
        flocksFragment = new FlocksFragment();
        athleteWorkoutsFragment = new AthleteWorkoutsFragment();
    }


    public void changeFragment(FragmentActivity activity, Fragment newFragment, int containerId) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerId, newFragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toast.makeText(this, PreferenceManager.getDefaultSharedPreferences(this).getString("loggedInUserName",""), Toast.LENGTH_SHORT).show();
        initializeObjects();
        InitializeFragments();
        bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getTitle().toString()){
                    case "Connect Athlete":
                        changeFragment(MainPageActivity.this, connectToAthleteFragment, R.id.nav_host_fragment);
                        break;

                    case "Profile":
                        changeFragment(MainPageActivity.this, profileFragment, R.id.nav_host_fragment);
                        break;
                    case "Connect":
                        changeFragment(MainPageActivity.this,connectCoachFragment,R.id.nav_host_fragment);
                    break;
                    case "Athletes":
                        changeFragment(MainPageActivity.this,myAthletesFragment,R.id.nav_host_fragment);
                    break;
                    case "Flocks":
                        changeFragment(MainPageActivity.this,flocksFragment,R.id.nav_host_fragment);
                        break;

                    case "Activities":
                        changeFragment(MainPageActivity.this, athleteWorkoutsFragment,R.id.nav_host_fragment);
                    break;
                }



                return true;
            }
        });
        // Clear the bottom nav menu safely
        changeFragment(this, profileFragment, R.id.nav_host_fragment);

        runOnUiThread(() -> bottomNavBar.getMenu().clear());


        new Thread(() ->{
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainPageActivity.this);
            String role =   ApiService.getRole(pref.getString("apiKey",""));
            System.out.println(role);
            runOnUiThread( () ->{

                bottomNavBar.inflateMenu(role.equals("athlete")
                      ?
                                R.menu.athlete_bottom_nav_menu :
                                R.menu.coach_bottom_nav_menu
                );

            });





        }).start();

    }
}

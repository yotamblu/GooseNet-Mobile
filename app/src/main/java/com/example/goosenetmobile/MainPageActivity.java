package com.example.goosenetmobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainPageActivity extends AppCompatActivity {

    BottomNavigationView bottomNavBar;
    OkHttpClient client = new OkHttpClient(); // Reuse this instance

    private void initializeObjects() {
        bottomNavBar = findViewById(R.id.bottom_navigation);
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

        initializeObjects();

        // Clear the bottom nav menu safely
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

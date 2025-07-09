package com.example.goosenetmobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import de.hdodenhof.circleimageview.CircleImageView;

public class AthleteProfileActivity extends AppCompatActivity {

    private Button addWorkoutBtn;
    private Button addToFlockButton;

    private void InitializeButtonObjects(){
        addWorkoutBtn = findViewById(R.id.btnAddWorkout);
        addToFlockButton  = findViewById(R.id.btnAddToFlock);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_athlete_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle intentData = getIntent().getExtras();
        String athleteName = intentData.getString("athleteName");
        String imageData = intentData.getString("imageData");

        TextView athleteNameTextView = findViewById(R.id.usernameText);
        athleteNameTextView.setText(athleteName);
        CircleImageView profileImageView = findViewById(R.id.profileImage);
        Bitmap bmp = GooseNetUtil.base64ToBitmap(imageData);
        if (bmp != null) {
            profileImageView.setImageBitmap(bmp);
        } else {
            profileImageView.setImageResource(R.drawable.loading);
        }
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        InitializeButtonObjects();
        addWorkoutBtn.setOnClickListener(v ->{
            Intent intent = new Intent(AthleteProfileActivity.this, AddWorkoutActivity.class);
            intent.putExtra("athleteName",athleteName);
            startActivity(intent);
        });

        addToFlockButton.setOnClickListener(v ->{
            Intent intent = new Intent(AthleteProfileActivity.this, AddToFlockActivity.class);
            intent.putExtra("athleteName",athleteName);
            startActivity(intent);

        });

    }
}
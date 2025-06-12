package com.example.goosenetmobile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class LauncherActivity extends AppCompatActivity {

    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("goose_animation.json");
        animationView.setBackgroundColor(Color.TRANSPARENT);

        animationView.playAnimation();
        animationView.loop(false);
        getPreferences(0).edit().clear().apply();
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(!GooseNetUtil.isLoggedIn(LauncherActivity.this)){
                    startActivity(new Intent(LauncherActivity.this,LoginActivity.class));
                }else{
                    //TODO: initialize moving to main activity if logged in (with intent)
                }

                finish();
            }
        });


    }
}
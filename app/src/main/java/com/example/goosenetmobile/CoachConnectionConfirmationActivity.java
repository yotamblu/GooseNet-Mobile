package com.example.goosenetmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CoachConnectionConfirmationActivity extends AppCompatActivity {

    private Button btnConfirm, btnCancel;
    private TextView confirmationText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_coach_connection_confirmation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Bundle extras = getIntent().getExtras();
        String coachName = extras.getString("coachName");
        String coachId = extras.getString("coachId");
        btnConfirm  = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);
        confirmationText =findViewById(R.id.confirmationMessage);
        confirmationText.setText("Are you sure you want to connect with coach " + coachName + "?");
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(() ->{
                    boolean request =  ApiService.connectToCoach(coachId,CoachConnectionConfirmationActivity.this);

                    runOnUiThread(() -> {

                        setResult(request ? RESULT_OK : RESULT_CANCELED);
                        finish();
                    });
                }).start();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }
}
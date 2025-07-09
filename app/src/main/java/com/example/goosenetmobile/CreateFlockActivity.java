package com.example.goosenetmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateFlockActivity extends AppCompatActivity {

    private Button submitButton;

    private EditText flockNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_flock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        submitButton = findViewById(R.id.createFlockSubmitButton);
        flockNameEditText = findViewById(R.id.flockNameEditText);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String flockName = flockNameEditText.getText().toString();
                if(flockName.equals("")){
                    Toast.makeText(CreateFlockActivity.this,
                            "Please enter a flock name", Toast.LENGTH_SHORT).show();
                }else if(containsSpecialChar(flockName)){
                    Toast.makeText(CreateFlockActivity.this,
                            "Flock Name mustn't contain .,$,#, [,],/", Toast.LENGTH_SHORT).show();
                }
                new Thread(()->{
                    if(ApiService.createFlock(flockName,CreateFlockActivity.this)){
                        runOnUiThread(() -> {
                            Toast.makeText(CreateFlockActivity.this,
                                    "Flock Created Successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();

                        });
                    }else{
                        runOnUiThread(() ->{
                            Toast.makeText(CreateFlockActivity.this,
                                    "You already have a flock with this name", Toast.LENGTH_SHORT).show();
                             flockNameEditText.setText("");
                        }
                    );

                    }
                }).start();
            }
        });


    }
    public static boolean containsSpecialChar(String input) {
        // Define the special characters
        String specialChars = ".,$#[]/";

        for (char c : input.toCharArray()) {
            if (specialChars.indexOf(c) != -1) {
                return true;
            }
        }

        return false;
    }

}
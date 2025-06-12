package com.example.goosenetmobile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    final int REGISTRED_SUCCESSFULLY = 0;
    private Dialog progressDialog;
    private Button loginButton;
    private EditText userNameInput, passwordInput;
    private TextView wrongCredsTV;
    private TextView goToReg;
    private void showBlockingProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View view = LayoutInflater.from(this).inflate(R.layout.progress_loader, null);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false); // Block back button
            progressDialog.setContentView(view);
        }
        progressDialog.show();
    }

    private boolean isLoginFormValid() {
        String username = userNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        return !username.isEmpty() && !password.isEmpty();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REGISTRED_SUCCESSFULLY && resultCode == RESULT_OK) {
            finish(); // Finish login after successful registration
        }
    }

    private void hideBlockingProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void InitializeObjects (){
        loginButton = findViewById(R.id.login_button);
        passwordInput = findViewById(R.id.password_input);
        userNameInput = findViewById(R.id.username_input);
        wrongCredsTV = findViewById(R.id.invalidCredsTV);
        goToReg = findViewById(R.id.register_text);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeObjects();
        goToReg.setOnClickListener(v ->
                startActivityForResult(new Intent(LoginActivity.this,RegisterActivity.class),REGISTRED_SUCCESSFULLY));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoginFormValid()){
                    showBlockingProgressDialog();
                    new Thread(()->{
                        try {
                            if(ApiService.AuthUser(userNameInput.getText().toString(),passwordInput.getText().toString())){
                                //TODO: Add moving to the main page activity
                                String apiKey = null;
                                try {
                                    System.out.println(userNameInput.getText().toString());
                                    System.out.println(GooseNetUtil.sha256(passwordInput.getText().toString()));
                                    apiKey = ApiService.getApiKey(userNameInput.getText().toString()
                                            , GooseNetUtil.sha256(passwordInput.getText().toString()));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                                GooseNetUtil.LogUserIn(userNameInput.getText().toString(),apiKey,LoginActivity.this);

                                runOnUiThread(() ->{


                                    startActivity(new Intent(LoginActivity.this,MainPageActivity.class));
                                    finish();
                                    hideBlockingProgressDialog();

                                });

                            }else{
                                runOnUiThread(() ->  {
                                    hideBlockingProgressDialog();
                                    wrongCredsTV.setVisibility(View.VISIBLE);
                                });
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                    }).start();
                }

            }
        });



    }
}
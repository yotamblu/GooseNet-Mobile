package com.example.goosenetmobile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goosenetmobile.classes.User;


public class RegisterActivity extends AppCompatActivity {

    private Spinner roleSpinner;

    private Dialog progressDialog;


    private EditText userNameInput
            ,passwordInput
            ,fullNameInput
            ,emailInput;

    private ImageView backButton;

    private Button registerButton;

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



    private void hideBlockingProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private boolean isRegistrationFormValid() {
        String username = userNameInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }

        if (role.equals("Select Role")) {  // Adjust if you have a different default hint
            return false;
        }

        return true;
    }

    private void InitializeObjects() {
        userNameInput = findViewById(R.id.username_input);
        roleSpinner = findViewById(R.id.role_spinner);
        passwordInput = findViewById(R.id.password_input);
        fullNameInput = findViewById(R.id.fullname_input);
        emailInput = findViewById(R.id.email_input);
        backButton = findViewById(R.id.back_button);
        registerButton = findViewById(R.id.register_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        InitializeObjects();

        backButton.setOnClickListener(v -> finish());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        //handle register button press
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRegistrationFormValid()){
                    User userData = new User();
                    userData.setEmail(emailInput.getText().toString());
                    userData.setUserName(userNameInput.getText().toString());
                    userData.setRole(roleSpinner.getSelectedItem().toString().toLowerCase());
                    userData.setFullName(fullNameInput.getText().toString());
                    userData.setPassword(GooseNetUtil.sha256(passwordInput.getText().toString()));
                    showBlockingProgressDialog();
                    new Thread(() ->{
                        if(ApiService.RegisterUser(userData)){

                            String apiKey = null;
                            try {
                                Thread.sleep(3000);
                                System.out.println(userData.getUserName());
                                System.out.println(userData.getPassword());
                                apiKey = ApiService.getApiKey(userData.getUserName(),userData.getPassword());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            GooseNetUtil.LogUserIn(userData.getUserName(),apiKey,RegisterActivity.this);
                            setResult(RESULT_OK);
                            startActivity(new Intent(RegisterActivity.this,MainPageActivity.class));
                            finish();

                        }else{
                            runOnUiThread(() ->{

                                Toast.makeText(RegisterActivity.this, "Your UserName must be unique and without '.', '#', '$'", Toast.LENGTH_SHORT).show();
                                hideBlockingProgressDialog();
                            });

                        }
                    }).start();


                }else{
                    Toast.makeText(RegisterActivity.this, "All fields must be valid to register", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
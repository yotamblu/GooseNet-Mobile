package com.example.goosenetmobile;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText newPasswordField, confirmPasswordField;
    private MaterialButton submitButton;
    Dialog progressDialog;
    private void showBlockingProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View view = LayoutInflater.from(this).inflate(R.layout.progress_loader_change_password, null);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false); // Block back button
            progressDialog.setContentView(view);
        }
        progressDialog.show();
    }

    public void hideBlockingProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        newPasswordField = findViewById(R.id.newPasswordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        submitButton = findViewById(R.id.submitPasswordButton);

        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswords();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        newPasswordField.addTextChangedListener(passwordWatcher);
        confirmPasswordField.addTextChangedListener(passwordWatcher);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showBlockingProgressDialog();
                new Thread(() ->{
                    String newPassword = newPasswordField.getText() != null ? newPasswordField.getText().toString() : "";

                    String apiKey = PreferenceManager.getDefaultSharedPreferences(ChangePasswordActivity.this).getString("apiKey","");
                    boolean result = ApiService.changePassword(GooseNetUtil.sha256(newPassword),apiKey);
                    hideBlockingProgressDialog();
                    if(result){
                        runOnUiThread(() -> {
                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        });

                    }else{
                        runOnUiThread(() -> {
                            Toast.makeText(ChangePasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                        });
                    }

                }).start();
            }
        });
    }

    private void validatePasswords() {
        String newPassword = newPasswordField.getText() != null ? newPasswordField.getText().toString() : "";
        String confirmPassword = confirmPasswordField.getText() != null ? confirmPasswordField.getText().toString() : "";

        boolean isValid = newPassword.length() >= 8 && newPassword.equals(confirmPassword);
        submitButton.setEnabled(isValid);

    }
}

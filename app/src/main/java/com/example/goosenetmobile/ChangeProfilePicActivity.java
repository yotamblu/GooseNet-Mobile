package com.example.goosenetmobile;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChangeProfilePicActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private MaterialButton selectImageButton, saveButton, revertButton;
    private Uri selectedImageUri = null;
    private String base64Image = null; // <-- Base64 of selected image
    private final int DEFAULT_IMAGE = android.R.drawable.ic_menu_camera; // <-- placeholder default

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_pic);

        // Initialize Views
        profileImageView = findViewById(R.id.profileImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveButton = findViewById(R.id.saveButton);
        revertButton = findViewById(R.id.revertButton);

        // Image Picker Launcher
        ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                            profileImageView.setImageBitmap(bitmap);
                            saveButton.setEnabled(true);

                            // Convert to Base64
                            base64Image = convertBitmapToBase64(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Select from Gallery
        selectImageButton.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        // Revert to Default
        revertButton.setOnClickListener(v -> {
            profileImageView.setImageResource(DEFAULT_IMAGE);
            selectedImageUri = null;
            base64Image = null; // clearing encoded image
            saveButton.setEnabled(true); // allow save so backend knows to reset
        });

        // Save Button (simulate upload)
        saveButton.setOnClickListener(v -> {
            new Thread(() ->{
                String apiKey = PreferenceManager.getDefaultSharedPreferences(ChangeProfilePicActivity.this).getString("apiKey","");
                if(ApiService.changeProfilePic(base64Image,base64Image == null,apiKey)){
                    runOnUiThread(() ->{
                        Toast.makeText(ChangeProfilePicActivity.this, "Profile Picture Updated Successfully!", Toast.LENGTH_SHORT).show();

                    });
                }else{
                    runOnUiThread(() ->{
                        Toast.makeText(ChangeProfilePicActivity.this, "There was an error updating your profile picture.!", Toast.LENGTH_SHORT).show();
                    });

                }


            }).start();
           
            finish();
        });
    }

    // Helper: Convert bitmap to Base64 string
    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

}

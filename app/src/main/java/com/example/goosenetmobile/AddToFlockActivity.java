package com.example.goosenetmobile;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.goosenetmobile.classes.FlockCard;

import java.util.ArrayList;
import java.util.List;

public class AddToFlockActivity extends AppCompatActivity {

    private TextView title;
    private ListView flockCardsList;
    private Dialog progressDialog;
    private LinearLayout noMoreFlocks;

    private void showBlockingProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View view = LayoutInflater.from(this).inflate(R.layout.progress_loader_fetching_flocks, null);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(view);
        }
        progressDialog.show();
    }

    private void hideBlockingProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showBlockingProgressDialog2() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View view = LayoutInflater.from(this).inflate(R.layout.flock_addition_progress_loader, null);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setContentView(view);
        }
        progressDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_to_flock);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        String athleteName = extras.getString("athleteName", "");
        if (athleteName.isEmpty()) {
            finish();
            return;
        }

        noMoreFlocks = findViewById(R.id.noMoreFlocks);
        flockCardsList = findViewById(R.id.flockCardsListView);
        title = findViewById(R.id.addToFlockTextView);
        title.setText("Select Flock To add @" + athleteName + " To:");
        showBlockingProgressDialog();

        new Thread(() -> {
            try {
                List<String> potentialFlockNames = ApiService.getPotentialFlocks(athleteName, AddToFlockActivity.this);
                List<FlockCard> potentialFlockCards = new ArrayList<>();
                if (potentialFlockNames != null) {
                    for (String flockName : potentialFlockNames) {
                        potentialFlockCards.add(new FlockCard(flockName));
                    }
                }
                AddToFlockCardAdapter adapter = new AddToFlockCardAdapter(AddToFlockActivity.this, potentialFlockCards);
                runOnUiThread(() -> {
                    hideBlockingProgressDialog();
                    if (potentialFlockNames == null || potentialFlockNames.isEmpty()) {
                        noMoreFlocks.setVisibility(View.VISIBLE);
                    } else {
                        noMoreFlocks.setVisibility(View.GONE);
                    }
                    flockCardsList.setAdapter(adapter);
                });
            } catch (Exception e) {
                Log.e("AddToFlockActivity", "Failed to load flocks", e);
                runOnUiThread(() -> {
                    hideBlockingProgressDialog();
                    Toast.makeText(AddToFlockActivity.this, "Failed to load flocks", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}

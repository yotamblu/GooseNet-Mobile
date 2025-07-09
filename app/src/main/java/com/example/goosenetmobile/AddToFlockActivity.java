package com.example.goosenetmobile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
    private Dialog progressDialog2;
    private LinearLayout noMoreFlocks;
    private void showBlockingProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View view = LayoutInflater.from(this).inflate(R.layout.progress_loader_fetching_flocks, null);
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

    public void showBlockingProgressDialog2() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            View view = LayoutInflater.from(this).inflate(R.layout.flock_addition_progress_loader, null);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false); // Block back button
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
        String athleteName = getIntent().getExtras().get("athleteName").toString();
        noMoreFlocks = findViewById(R.id.noMoreFlocks);
        flockCardsList = findViewById(R.id.flockCardsListView);
        title = findViewById(R.id.addToFlockTextView);
        title.setText("Select Flock To add @" + athleteName + " To:");
        showBlockingProgressDialog();
        new Thread(() ->{

            List<String> potentialFlockNames = ApiService.getPotentialFlocks(athleteName,AddToFlockActivity.this);
            List<FlockCard> potentialFlockCards = new ArrayList<>();
            for (String flockName:
                 potentialFlockNames) {
                potentialFlockCards.add(new FlockCard(flockName));
            }
            AddToFlockCardAdapter adapter = new AddToFlockCardAdapter(AddToFlockActivity.this,potentialFlockCards);
            runOnUiThread(() -> {
                if(potentialFlockNames.size() > 0){
                     noMoreFlocks.setVisibility(View.GONE);
                }
                hideBlockingProgressDialog();
                flockCardsList.setAdapter(adapter);

            });
        }).start();









    }
}
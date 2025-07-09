package com.example.goosenetmobile;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mozilla.geckoview.GeckoResult;
import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;

import java.util.List;


public class ConnectToGarminActivity extends AppCompatActivity {
    private WebView webView;
    private static GeckoRuntime sRuntime;

    private void openCustomTab(String url) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
        try {
            intent.launchUrl(this, Uri.parse(url));
        } catch (Exception e) {
            // Chrome not available — fallback to WebView

            Toast.makeText(this, "chrome unAvailable", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect_to_garmin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });






        final String[][] oAuthTokenAndSecret = {{}}; // same as before

        new Thread(() -> {
            oAuthTokenAndSecret[0] = ApiService.getOAuthTokenAndSecret();
            String userVerificationUrl = "https://connect.garmin.com/oauthConfirm" +
                    "?oauth_token=" + oAuthTokenAndSecret[0][0]
                    + "&oauth_callback=about:blank";

            runOnUiThread(() -> {
                GeckoView view = findViewById(R.id.geckoview);
                GeckoSession session = new GeckoSession();

                session.setContentDelegate(new GeckoSession.ContentDelegate() {



                });

                session.setNavigationDelegate(new GeckoSession.NavigationDelegate() {
                    @Override
                    public void onLocationChange(@NonNull GeckoSession session, @Nullable String url, @NonNull List<GeckoSession.PermissionDelegate.ContentPermission> perms, @NonNull Boolean hasUserGesture) {
                        GeckoSession.NavigationDelegate.super.onLocationChange(session, url, perms, hasUserGesture);
                        if(url.startsWith("about:blank")){
                            new Thread(() ->{
                               String params = url.split("\\?")[1] +
                                       "&token_secret=" + oAuthTokenAndSecret[0][1]
                                       + "&apiKey=" + GooseNetUtil.getApiKey(ConnectToGarminActivity.this);
                               ApiService.connectToGarminAccount(params);
                               setResult(RESULT_OK);
                               finish();
                            }).start();
                        }
                    }
                });


                sRuntime = GeckoRuntime.create(this);

                session.open(sRuntime);
                view.setSession(session);
                session.loadUri(userVerificationUrl);
                System.out.println(userVerificationUrl);

            });
        }).start();
    }
}
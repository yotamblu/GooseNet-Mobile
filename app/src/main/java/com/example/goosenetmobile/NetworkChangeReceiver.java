package com.example.goosenetmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        if (!isOnline(context)) {
            showNoInternetDialog(context);
        }
    }

    private boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        // Should return true only if connected
        return (netInfo != null && netInfo.isConnected());
    }

    private void showNoInternetDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("No Connection")
                .setMessage("The internet is not connected. Most features of GooseNet will not work.")
                .setPositiveButton("Dismiss", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}

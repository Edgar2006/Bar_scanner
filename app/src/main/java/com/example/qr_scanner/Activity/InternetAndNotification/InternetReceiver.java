package com.example.qr_scanner.Activity.InternetAndNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

public class InternetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = CheckInternet.getNetworkInfo(context);
        if (status.equals("connected")){
        }
        else if (status.equals("disconnected")){
            Toast.makeText(context, "Not connected",Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }
}

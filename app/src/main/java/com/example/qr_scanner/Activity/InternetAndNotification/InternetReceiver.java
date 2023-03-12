package com.example.qr_scanner.Activity.InternetAndNotification;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.qr_scanner.Activity.User.Register;

public class InternetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = CheckInternet.getNetworkInfo(context);
        if (status.equals("connected")){
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if(cn.getClassName().contains("AlertDialogActivity")){
                Log.e("ActivityManager ", "ActivityManager" + cn);
                ActivityManager am1 = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn1 = am1.getRunningTasks(1).get(0).baseActivity;
                Log.e("ActivityManager ", "ActivityManager" + cn1);

            }
        }
        else if (status.equals("disconnected")){
            Toast.makeText(context, "Not connected",Toast.LENGTH_LONG).show();
            Intent i = new Intent(context, AlertDialogActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }





}

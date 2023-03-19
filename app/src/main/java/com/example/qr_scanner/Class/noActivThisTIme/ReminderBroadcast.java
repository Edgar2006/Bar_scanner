package com.example.qr_scanner.Class.noActivThisTIme;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.qr_scanner.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReminderBroadcast extends BroadcastReceiver {
    public static String barCode;
    public static PendingIntent pendingIntent;


    @Override
    public void onReceive(Context context, Intent intent) {

        f(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notification")
                .setSmallIcon(R.drawable.scan_icon)
                .setContentTitle("Your scan")
                .setGroupSummary(true)
                .setContentText("Hey you bought this product" + barCode)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setGroup("array")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify((int) System.currentTimeMillis(),builder.build());
    }
    public void f(Context context) {
        try {
            FileInputStream fileInput = context.getApplicationContext().openFileInput("Notification.txt");
            InputStreamReader reader = new InputStreamReader(fileInput);
            BufferedReader buffer = new BufferedReader(reader);
            ArrayList<String> list = new ArrayList<>();
            String lines;
            while ((lines = buffer.readLine()) != null) {
                list.add(lines);
            }
            barCode = list.get(0);

        }catch (Exception e){

        }
    }
}

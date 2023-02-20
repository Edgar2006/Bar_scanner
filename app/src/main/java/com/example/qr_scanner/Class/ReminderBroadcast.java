package com.example.qr_scanner.Class;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.qr_scanner.Activity.NewCommentActivity;
import com.example.qr_scanner.R;

public class ReminderBroadcast extends BroadcastReceiver {
    public static String barCode;
    public static PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notification")
                .setSmallIcon(R.drawable.scan_icon)
                .setContentTitle("Your scan")
                .setGroupSummary(true)
                .setContentText("Hey you bought this product" + barCode)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setGroup("array");

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify((int) System.currentTimeMillis(),builder.build());
    }
}

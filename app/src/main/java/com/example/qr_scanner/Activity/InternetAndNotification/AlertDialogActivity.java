package com.example.qr_scanner.Activity.InternetAndNotification;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.qr_scanner.R;

public class AlertDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        new CountDownTimer(Integer.MAX_VALUE, 3000) {
            public void onTick(long millisUntilFinished) {
                String status = CheckInternet.getNetworkInfo(getBaseContext());
                if (status.equals("connected")){
                    finish();
                }
            }
            public void onFinish() {
            }
        }.start();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Your internet connection is broken")
                .setMessage("Open settings?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    builder.getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    dialog.cancel();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    Toast.makeText(AlertDialogActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
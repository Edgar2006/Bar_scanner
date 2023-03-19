package com.example.qr_scanner.Activity.Company;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdminSendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_send);
        try {
            FileOutputStream fileOutputStream = openFileOutput(StaticString.Authentication, MODE_PRIVATE);
            fileOutputStream.write(StaticString.haveARating.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onCLickSendEmail(View view) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "edgar.bezhanyan@gmail.com" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Email Subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "send a verification company");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}
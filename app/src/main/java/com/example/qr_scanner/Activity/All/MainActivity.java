package com.example.qr_scanner.Activity.All;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.qr_scanner.Activity.Company.CompanyHomeActivity;
import com.example.qr_scanner.Activity.InternetAndNotification.InternetReceiver;
import com.example.qr_scanner.Activity.User.HomeActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            FileInputStream fileInput = openFileInput(StaticString.Authentication);
            InputStreamReader reader = new InputStreamReader(fileInput);
            BufferedReader buffer = new BufferedReader(reader);
            ArrayList<String> list = new ArrayList<>();
            String lines;
            while ((lines = buffer.readLine()) != null){
                list.add(lines);
            }
            if(list.size() == 3){
                String emailToString = list.get(0);
                String passwordToString = list.get(1);
                String type = list.get(2);
                Intent intent;
                if(Objects.equals(type, StaticString.company)){
                    User.COMPANY = Function.convertor(emailToString);
                    intent = new Intent(MainActivity.this, CompanyHomeActivity.class);
                }
                else{
                    intent = new Intent(MainActivity.this, HomeActivity.class);
                }
                intent.putExtra(StaticString.email,emailToString);
                intent.putExtra(StaticString.password,passwordToString);
                startActivity(intent);
            }
            else{
                logReg();
            }
        } catch (Exception e) {
            logReg();
        }



        init();
    }
    private void logReg(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this,Login_or_register.class);
            startActivity(intent);
        }, 1000);
    }
    private void init(){
        broadcastReceiver = new InternetReceiver();
        InternetStatus();
    }
    public void InternetStatus(){
        registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


}
package com.example.qr_scanner.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.qr_scanner.Activity.Company.CompanyEditActivity;
import com.example.qr_scanner.Activity.Company.CompanyHomeActivity;
import com.example.qr_scanner.Activity.User.HomeActivity;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        try {
            FileInputStream fileInput = openFileInput("Authentication.txt");
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
                mAuth.signInWithEmailAndPassword(emailToString, passwordToString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent;
                            if(Objects.equals(type, "Company")){
                                Toast.makeText(MainActivity.this, "1" + emailToString, Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, CompanyHomeActivity.class);
                            }
                            else{
                                Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                                intent = new Intent(MainActivity.this, HomeActivity.class);
                            }
                            intent.putExtra("email",emailToString);
                            intent.putExtra("password",passwordToString);
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(MainActivity.this,Login_or_register.class);
                            startActivity(intent);
                        }
                    }
                });
            }
            else{
                logReg();
            }
        } catch (Exception e) {
            logReg();
        }
    }
    private void logReg(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(MainActivity.this,Login_or_register.class);
                startActivity(intent);
            }
        }, 1000);
    }
}
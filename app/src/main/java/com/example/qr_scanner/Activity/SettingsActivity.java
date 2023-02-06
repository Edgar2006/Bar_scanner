package com.example.qr_scanner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.qr_scanner.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }
    private void init(){
        mAuth = FirebaseAuth.getInstance();
    }
    public void onClickLogout(View view){
        try {
            String temp = "";
            FileOutputStream fileOutputStream = openFileOutput("Authentication.txt", MODE_PRIVATE);
            fileOutputStream.write(temp.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mAuth.signOut();
        Intent intent = new Intent(SettingsActivity.this, Login_or_register.class);
        startActivity(intent);
    }
}
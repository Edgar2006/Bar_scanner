package com.example.qr_scanner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private Button nextStep;
    private String emailToString,passwordToString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        nextStep = findViewById(R.id.start_activity_read);
        addLocalData();
    }
    public void onCLickNextStep(View view){
        Intent intent = new Intent(HomeActivity.this,ScanActivity.class);
        startActivity(intent);
    }
    public void addLocalData(){
        Intent intent = getIntent();
        if (intent != null) {
            emailToString = intent.getStringExtra("email");
            passwordToString = intent.getStringExtra("password");
            User.EMAIL = emailToString;
            User.PASSWORD = passwordToString;
            User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
            try {
                String newUser = emailToString + "\n" + passwordToString;
                Toast.makeText(this, newUser, Toast.LENGTH_SHORT).show();
                FileOutputStream fileOutputStream = openFileOutput("Authentication.txt", MODE_PRIVATE);
                fileOutputStream.write(newUser.getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
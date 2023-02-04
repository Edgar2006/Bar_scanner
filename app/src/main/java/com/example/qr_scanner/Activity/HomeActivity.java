package com.example.qr_scanner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {
    private Button nextStep;
    private String email_txt,password_txt;
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
        User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
        Intent intent = getIntent();
        if (intent != null) {
            email_txt = intent.getStringExtra("email");
            password_txt = email_txt + "\n" + intent.getStringExtra("password");
            User.EMAIL = email_txt;
            try {
                FileOutputStream fileOutputStream = openFileOutput("Authentication.txt", MODE_PRIVATE);
                fileOutputStream.write(password_txt.getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
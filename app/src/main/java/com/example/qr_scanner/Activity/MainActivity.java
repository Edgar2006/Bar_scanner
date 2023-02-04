package com.example.qr_scanner.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.User;
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
            if(list.size() == 2){
                String email_txt = list.get(0);
                String password_txt = list.get(1);
                mAuth.signInWithEmailAndPassword(email_txt,password_txt)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    User.EMAIL = Function.convertor(email_txt);
                                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                                    intent.putExtra("email",email_txt);
                                    intent.putExtra("password",password_txt);
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
                Intent intent = new Intent(MainActivity.this,Login_or_register.class);
                startActivity(intent);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
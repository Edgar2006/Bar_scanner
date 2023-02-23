package com.example.qr_scanner.Class;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_scanner.DataBase_Class.User;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Function  extends AppCompatActivity {

    public Function() {
    }

    public static String convertor(String a){
        return a.replace(".", "|");
    }
    public void addLocalData(Intent intent){
        if (intent != null) {
            String emailToString = intent.getStringExtra("email");
            String passwordToString = intent.getStringExtra("password");
            User.EMAIL = emailToString;
            User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
            try {
                String newUser = emailToString + "\n" + passwordToString;
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

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



}

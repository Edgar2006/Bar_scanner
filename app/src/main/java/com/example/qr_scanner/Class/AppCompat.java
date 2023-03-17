package com.example.qr_scanner.Class;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_scanner.DataBase_Class.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AppCompat extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageManger languageManger = new LanguageManger(this);
        languageManger.updateResource(languageManger.getLang());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
            case 1:
                if(resultCode == RESULT_OK){
                    User.URL = String.valueOf(imageReturnedIntent.getData());
                    Toast.makeText(this, User.URL, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

}

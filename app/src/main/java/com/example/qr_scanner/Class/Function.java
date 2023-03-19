package com.example.qr_scanner.Class;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_scanner.R;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;
import java.util.Objects;

public class Function  extends AppCompatActivity{

    public Function() {
    }



    public Function(int contentLayoutId) {
        super(contentLayoutId);
    }




    public static String CONVERTOR(String s){
        return s.replace(".", "|");
    }
    public static String POP(String v){
        StringBuilder s = new StringBuilder();
        int q = v.length();
        boolean b=false;
        for(int i=v.length()-1;i>=0;i--){
            if(v.charAt(i) != ' '){
                q = i+1;
                break;
            }
        }
        for(int i=0;i<q;i++){
            if(v.charAt(i) != ' '){
                b=true;
            }
            if (b){
                s.append(v.charAt(i));
            }
        }
        return String.valueOf(s);
    }


}

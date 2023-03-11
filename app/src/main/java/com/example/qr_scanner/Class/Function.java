package com.example.qr_scanner.Class;

import androidx.appcompat.app.AppCompatActivity;

public class Function  extends AppCompatActivity {

    public Function() {
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

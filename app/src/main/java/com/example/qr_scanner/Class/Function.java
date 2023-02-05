package com.example.qr_scanner.Class;

public class Function {


    public static String convertor(String a){
        try {
            return a.replace(".", "|");
        }catch (Exception e){
            return a;
        }
    }

}

package com.example.qr_scanner.Class;

public class Function {


    public static String convertor(String a){
        return a.replace(".", "|");
    }
    public static String dataTimeConvertor(String a){
        return a.replace("-", "_");
    }

}

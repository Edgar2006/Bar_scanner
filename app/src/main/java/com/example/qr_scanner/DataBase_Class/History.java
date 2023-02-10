package com.example.qr_scanner.DataBase_Class;

public class History {
    private String barCode;
    private long time;

    public History(String barCode, long time) {
        this.barCode = barCode;
        this.time = time;
    }
    public History(){

    }
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

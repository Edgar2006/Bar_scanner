package com.example.qr_scanner.Class.noActivThisTIme;

import com.example.qr_scanner.DataBase_Class.History;
import com.example.qr_scanner.DataBase_Class.Messenger;

import java.util.Comparator;

public class TimeComparator implements Comparator<Messenger> {
    @Override
    public int compare(Messenger a, Messenger b) {
        long a1 = a.getTime();
        long b1 = b.getTime();
        return a1 > b1 ? -1 : a1 == b1 ? 0 : 1;
    }
}
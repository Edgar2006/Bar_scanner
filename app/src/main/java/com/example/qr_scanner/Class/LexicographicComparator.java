package com.example.qr_scanner.Class;

import com.example.qr_scanner.DataBase_Class.Messenger;

import java.util.Comparator;

public class LexicographicComparator implements Comparator<Messenger> {
    @Override
    public int compare(Messenger a, Messenger b) {
        int a1 = Integer.parseInt(a.getCount());
        int b1 = Integer.parseInt(b.getCount());
        return a1 > b1 ? -1 : a1 == b1 ? 0 : 1;
    }
}
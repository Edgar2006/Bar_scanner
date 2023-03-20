package com.example.qr_scanner.Class.noActivThisTIme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qr_scanner.DataBase_Class.ProductBio;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TimeCompByHistory implements Comparator<ProductBio>{
    @Override
    public int compare(ProductBio a, ProductBio b) {
        long a1 = Long.parseLong(a.getCompanyRef());
        long b1 = Long.parseLong(b.getCompanyRef());
        return a1 > b1 ? -1 : a1 == b1 ? 0 : 1;
    }


}

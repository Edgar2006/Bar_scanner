package com.example.qr_scanner.DataBase_Class;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_scanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class GenRemoteDataSource<E>
{
    private final Class<E> clazz;

    public GenRemoteDataSource(Class<E> clazz)
    {
        this.clazz = clazz;
    }

    public void getDataFromDataBase(RecyclerView listView, RecyclerView.Adapter viewAdapter, ArrayList<E> listData, DatabaseReference reference, RelativeLayout activity, ProgressBar progressBar){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    E history = ds.getValue(clazz);
                    assert  history != null;
                    listData.add(history);
                }
                listView.setAdapter(viewAdapter);
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    activity.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }, 100);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(eventListener);
    }
    public void getDataFromDataBase(RecyclerView listView, RecyclerView.Adapter viewAdapter, ArrayList<ProductBio> listData, DatabaseReference reference, String email, RelativeLayout activity, ProgressBar progressBar){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    ProductBio history = ds.getValue(ProductBio.class);
                    assert  history != null;
                    if(Objects.equals(history.getCompanyEmail(), email)){
                        listData.add(history);
                    }
                }
                listView.setAdapter(viewAdapter);
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    activity.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }, 100);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(eventListener);
    }

}
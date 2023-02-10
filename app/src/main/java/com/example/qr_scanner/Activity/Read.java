package com.example.qr_scanner.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Read extends AppCompatActivity {
    private boolean sortMethod;
    private RecyclerView listView;
    private ViewAdapter viewAdapter;
    private ArrayList<Messenger> listData;
    private String bareCode;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        init();
        getDataFromDataBase();
    }
    private void init(){
        sortMethod = false;
        listView = findViewById(R.id.recView);
        listData = new ArrayList<Messenger>();
        viewAdapter = new ViewAdapter(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);
        Intent intent = getIntent();
        if(intent!=null){
            bareCode = intent.getStringExtra("bareCode");
        }
        reference  = FirebaseDatabase.getInstance().getReference("Product").child(bareCode);



    }

    public void onClickComment(View view){
        Intent intent = new Intent(Read.this,NewCommentActivity.class);
        intent.putExtra("barCode", bareCode);
        startActivity(intent);
    }
    public void onClickSortByLike(View view){
        sortMethod=true;
        sortMethod();
    }
    public void onClickSortByTime(View view){
        sortMethod=false;
        sortMethod();
    }
    public void sortMethod(){
        if(sortMethod) {
            Collections.sort(listData, new LexicographicComparator());
            listView.setAdapter(viewAdapter);
        }
        else{
            Collections.sort(listData, new TimeComparator());
            listView.setAdapter(viewAdapter);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu,menu);
        return true;
    }

    private  void  getDataFromDataBase(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    Messenger messenger = ds.getValue(Messenger.class);
                    assert  messenger != null;
                    String emailToString = messenger.getEmail();
                    listData.add(messenger);
                }
                sortMethod();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(eventListener);
    }



    private static class LexicographicComparator implements Comparator<Messenger> {
        @Override
        public int compare(Messenger a, Messenger b) {
            int a1 = Integer.parseInt(a.getCount());
            int b1 = Integer.parseInt(b.getCount());
            return a1 > b1 ? -1 : a1 == b1 ? 0 : 1;
        }
    }
    private static class TimeComparator implements Comparator<Messenger> {
        @Override
        public int compare(Messenger a, Messenger b) {
            long a1 = a.getTime();
            long b1 = b.getTime();
            return a1 > b1 ? -1 : a1 == b1 ? 0 : 1;
        }
    }



}
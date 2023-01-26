package com.example.qr_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.qr_scanner.Class.Friend;
import com.example.qr_scanner.Class.Messenger;
import com.example.qr_scanner.Class.View_Adapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Read extends AppCompatActivity {
    private RecyclerView listView;
    private View_Adapter view_adapter;
    private ArrayList<Friend> listData;
    private ArrayList<String> listFriends;
    private String bareCode;
    private FirebaseAuth mAuth;
    private DatabaseReference reference,friendsReference;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        init();
        getDataFromDataBase();
    }
    private void init(){
        listView = findViewById(R.id.recView);
        listData = new ArrayList<Friend>();
        listFriends = new ArrayList<String>();
        view_adapter = new View_Adapter(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(view_adapter);

        Intent intent = getIntent();
        if(intent!=null){
            bareCode = intent.getStringExtra("bareCode");
        }


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference  = FirebaseDatabase.getInstance().getReference("Product").child(bareCode);
        friendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(bareCode);
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
                    String email_txt = messenger.getEmail();
                    friendsReference.child(convertor(email_txt));
                    listData.add(new Friend(new Messenger(messenger.getAddress(),messenger.getComment(),email_txt),listFriends));
                }
                Collections.sort(listData,new LexicographicComparator());
                listView.setAdapter(view_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addValueEventListener(eventListener);
    }

    private void setOnClickItem(){
    }

    public String convertor(String a){
        return a.replace(".", "|");
    }

    class LexicographicComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend a, Friend b) {
            return a.getFriend_list().size() > b.getFriend_list().size() ? -1 : a.getFriend_list().size() == b.getFriend_list().size() ? 0 : 1;}
    }

}
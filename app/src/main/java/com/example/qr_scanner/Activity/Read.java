package com.example.qr_scanner.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.Friend;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Read extends AppCompatActivity {
    private RecyclerView listView;
    private ViewAdapter viewAdapter;
    private ArrayList<Friend> listData;
    private String bareCode;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference reference,friendsReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        init();
        reference  = database.getReference("Product").child(bareCode);
        friendsReference = database.getReference("Friends").child(bareCode);


        getDataFromDataBase();
    }
    private void init(){
        listView = findViewById(R.id.recView);
        listData = new ArrayList<Friend>();
        viewAdapter = new ViewAdapter(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);
/*        Intent intent = getIntent();
        if(intent!=null){
            bareCode = intent.getStringExtra("bareCode");
        }*/
        bareCode = "4602159014264";
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference  = database.getReference("Product").child(bareCode);
        friendsReference = database.getReference("Friends").child(bareCode);



    }


    private  void  getDataFromDataBase(){
            reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(Read.this, "!", Toast.LENGTH_SHORT).show();

                if(listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    Messenger messenger = ds.getValue(Messenger.class);
                    assert  messenger != null;
                    String emailToString = messenger.getEmail();
                    friendsReference.child(Function.convertor(emailToString));
                    listData.add(new Friend(new Messenger(messenger.getEmail(),messenger.getComment(),messenger.getAddress(),messenger.getCount()),friendsReference));
                }
                Collections.sort(listData, new LexicographicComparator());
                listView.setAdapter(viewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Read.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(Read.this, "_", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static class LexicographicComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend a, Friend b) {
            int a1 = Integer.parseInt(a.getMessenger().getCount());
            int b1 = Integer.parseInt(b.getMessenger().getCount());
            return a1 > b1 ? -1 : a1 == b1 ? 0 : 1;
        }
    }



}
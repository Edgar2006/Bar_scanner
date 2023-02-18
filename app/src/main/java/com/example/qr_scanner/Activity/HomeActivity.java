package com.example.qr_scanner.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.Adapter.ViewAdapterHome;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.History;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private String emailToString,passwordToString;
    private RecyclerView listView;
    private ViewAdapterHome viewAdapter;
    private ArrayList<History> listData;
    private DatabaseReference referenceHistory;
    private String uploadUri;
    private ImageView imageDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addLocalData();
        init();
        getDataFromDataBase();
    }

    public void init(){
        imageDataBase = findViewById(R.id.profile_image);
        listView = findViewById(R.id.recView);
        listData = new ArrayList<>();
        viewAdapter = new ViewAdapterHome(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);
        referenceHistory = FirebaseDatabase.getInstance().getReference("History").child(User.EMAIL_CONVERT);
        readUser();

    }
    public void readUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference("User").child(User.EMAIL_CONVERT);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user == null){
                    Intent intent1 = new Intent(HomeActivity.this,Login_or_register.class);
                    startActivity(intent1);
                }
                User.NAME = user.getName();
                User.URL = user.getImageRef();
                uploadUri = user.getImageRef();
                if(!Objects.equals(uploadUri, "noImage")) {
                    Picasso.get().load(uploadUri).into(imageDataBase);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myUserRef.addValueEventListener(postListener);
    }

    public void onCLickNextStep(View view){
        Intent intent = new Intent(HomeActivity.this,ScanActivity.class);
        startActivity(intent);
    }
    public void onCLickSetting(View view){
        Intent intent1 = new Intent(HomeActivity.this,SettingsActivity.class);
        startActivity(intent1);
    }

    public void addLocalData(){
        Intent intent = getIntent();
        if (intent != null) {
            emailToString = intent.getStringExtra("email");
            passwordToString = intent.getStringExtra("password");
            User.EMAIL = emailToString;
            User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
            try {
                String newUser = emailToString + "\n" + passwordToString;
                FileOutputStream fileOutputStream = openFileOutput("Authentication.txt", MODE_PRIVATE);
                fileOutputStream.write(newUser.getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private  void  getDataFromDataBase(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    History history = ds.getValue(History.class);
                    assert  history != null;
                    listData.add(history);
                }
                sortMethod();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceHistory.addValueEventListener(eventListener);

    }
    public void sortMethod(){
        Collections.sort(listData, new TimeComparator());
        listView.setAdapter(viewAdapter);
    }
    private static class TimeComparator implements Comparator<History> {
        @Override
        public int compare(History a, History b) {
            long a1 = a.getTime();
            long b1 = b.getTime();
            return a1 > b1 ? -1 : a1 == b1 ? 0 : 1;
        }
    }

}

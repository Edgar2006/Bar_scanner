package com.example.qr_scanner.Activity.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.All.Login_or_register;
import com.example.qr_scanner.Adapter.ViewAdapterCompanyByUser;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.GenRemoteDataSource;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView listView;
    private RecyclerView.Adapter viewAdapter;
    private ArrayList<ProductBio> listData;
    private String uploadUri;
    private ImageView imageDataBase;
    private TextView yourName;
    private RelativeLayout activity;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addLocalData();
        init();
        getDataFromDataBase();
    }

    public void init(){
        load();
        yourName = findViewById(R.id.your_name);
        imageDataBase = findViewById(R.id.profile_image);
        listView = findViewById(R.id.rec_view);
        listData = new ArrayList<>();
        viewAdapter = new ViewAdapterCompanyByUser(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);
        readUser();
    }
    public void readUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(StaticString.user).child(User.EMAIL_CONVERT);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user == null){
                    Intent intent1 = new Intent(HomeActivity.this, Login_or_register.class);
                    startActivity(intent1);
                }
                User.NAME = user.getName();
                User.URL = user.getImageRef();
                yourName.setText(user.getName());
                if(!Objects.equals(uploadUri, StaticString.noImage)) {
                    uploadUri = user.getImageRef();
                    Glide.with(HomeActivity.this).load(uploadUri).into(imageDataBase);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myUserRef.addValueEventListener(postListener);
    }

    public void onCLickNextStep(View view){
        Intent intent = new Intent(HomeActivity.this, ScanActivity.class);
        startActivity(intent);
    }
    public void onCLickSetting(View view){
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void addLocalData(){
        Intent intent = getIntent();
        if (intent != null) {
            String emailToString = intent.getStringExtra(StaticString.email);
            if(emailToString != null){
                String passwordToString = intent.getStringExtra(StaticString.password);
                User.EMAIL = emailToString;
                User.EMAIL_CONVERT = Function.CONVERTOR(User.EMAIL);
                String type = StaticString.user;
                try {
                    String newUser = emailToString + "\n" + passwordToString + "\n" + type;
                    FileOutputStream fileOutputStream = openFileOutput(StaticString.Authentication, MODE_PRIVATE);
                    fileOutputStream.write(newUser.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private  void  getDataFromDataBase(){
        DatabaseReference referenceHistory = FirebaseDatabase.getInstance().getReference("History").child(User.EMAIL_CONVERT);
        GenRemoteDataSource genRemoteDataSource = new GenRemoteDataSource(ProductBio.class);
        genRemoteDataSource.getDataFromDataBase(listView,viewAdapter,listData,referenceHistory,activity,progressBar);
    }

    private void load(){
            activity = findViewById(R.id.activity);
            activity.setVisibility(View.GONE);
            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
    }
    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }
}

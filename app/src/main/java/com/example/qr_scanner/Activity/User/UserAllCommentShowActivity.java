package com.example.qr_scanner.Activity.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.LexicographicComparator;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.Class.TimeComparator;
import com.example.qr_scanner.DataBase_Class.GenRemoteDataSource;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class UserAllCommentShowActivity extends AppCompatActivity {
    private TextView name;
    private ImageView imageView;
    private String email;
    private RecyclerView listView;
    private ViewAdapter viewAdapter;
    private ArrayList<Messenger> listData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_comment_show);
        init();
        setUseIntent();
        Handler handler = new Handler();
        handler.postDelayed(() -> getDataFromDataBase(), 1000);
    }
    private void init(){
        name = findViewById(R.id.user_name);
        imageView = findViewById(R.id.user_image);
        listView = findViewById(R.id.rec_view);
        listData = new ArrayList<>();
        viewAdapter = new ViewAdapter(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);

    }
    private void setUseIntent(){
        Intent intent = getIntent();
        name.setText(intent.getStringExtra(StaticString.user));
        String userImageUrl = intent.getStringExtra(StaticString.userImage);
        if(!Objects.equals(userImageUrl, StaticString.noImage)) {
            Glide.with(UserAllCommentShowActivity.this).load(userImageUrl).into(imageView);
        }
        email = intent.getStringExtra(StaticString.email);
    }

    private void getDataFromDataBase(){
        DatabaseReference referenceComment = FirebaseDatabase.getInstance().getReference(StaticString.userComment).child(Function.convertor(email));
        GenRemoteDataSource genRemoteDataSource = new GenRemoteDataSource(Messenger.class);
        genRemoteDataSource.getDataFromDataBase(listView,viewAdapter,listData,referenceComment);
    }



}
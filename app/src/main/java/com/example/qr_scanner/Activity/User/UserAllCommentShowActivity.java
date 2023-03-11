package com.example.qr_scanner.Activity.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.GenRemoteDataSource;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class UserAllCommentShowActivity extends AppCompatActivity {
    private TextView name;
    private ImageView imageView;
    private String email;
    private RecyclerView listView;
    private ViewAdapter viewAdapter;
    private ArrayList<Messenger> listData;
    private RelativeLayout activity;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_comment_show);
        init();
        load();
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
        DatabaseReference referenceComment = FirebaseDatabase.getInstance().getReference(StaticString.userComment).child(Function.CONVERTOR(email));
        GenRemoteDataSource genRemoteDataSource = new GenRemoteDataSource(Messenger.class);
        genRemoteDataSource.getDataFromDataBase(listView,viewAdapter,listData,referenceComment,activity,progressBar);
    }

    private void load(){
        activity = findViewById(R.id.activity);
        activity.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

}
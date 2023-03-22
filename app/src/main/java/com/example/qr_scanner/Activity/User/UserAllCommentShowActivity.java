package com.example.qr_scanner.Activity.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.qr_scanner.Adapter.ViewAdapterUserAllCommentShow;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.UserCommentSaveData;
import com.example.qr_scanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UserAllCommentShowActivity extends AppCompatActivity {
    private TextView name,userReviewCount;
    private ImageView imageView;
    private String email;
    private RecyclerView listView;
    private ViewAdapterUserAllCommentShow viewAdapter;
    private ArrayList<Messenger> listData;
    private RelativeLayout activity;
    private ProgressBar progressBar;
    private DatabaseReference referenceCompany,referenceComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_comment_show);
        init();
        load();
        setUseIntent();
        Handler handler = new Handler();
        handler.postDelayed(() -> getDataFromDataBase(), 2000);
    }
    private void init(){
        userReviewCount = findViewById(R.id.user_review_count_num);
        activity = findViewById(R.id.activity);
        progressBar = findViewById(R.id.progress_bar);
        name = findViewById(R.id.user_name);
        imageView = findViewById(R.id.user_image);
        listView = findViewById(R.id.rec_view);
        listData = new ArrayList<>();
        viewAdapter = new ViewAdapterUserAllCommentShow(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);

    }
    private void setUseIntent(){
        Intent intent = getIntent();
        name.setText(intent.getStringExtra(StaticString.user));
        String userImageUrl = intent.getStringExtra(StaticString.userImage);
        if(!Objects.equals(userImageUrl, StaticString.noImage)) {
            Glide.with(getApplicationContext()).load(userImageUrl).into(imageView);
        }
        email = intent.getStringExtra(StaticString.email);
    }

    private void getDataFromDataBase(){
        DatabaseReference referenceAllComment = FirebaseDatabase.getInstance().getReference(StaticString.userComment).child(Function.CONVERTOR(email));
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    UserCommentSaveData userCommentSaveData = ds.getValue(UserCommentSaveData.class);
                    assert  userCommentSaveData != null;
                    Log.e("__________________________",userCommentSaveData.getBarCode());
                    referenceCompany = FirebaseDatabase.getInstance().getReference(StaticString.productBio).child(userCommentSaveData.getBarCode());
                    referenceComment  = FirebaseDatabase.getInstance().getReference(StaticString.product).child(userCommentSaveData.getBarCode()).child(userCommentSaveData.getUserEmail());
                    Messenger messenger = new Messenger(StaticString.haveARating);
                    messenger = getCompany(messenger);
                    messenger = getComment(messenger);
                    listData.add(messenger);

                }
                listView.setAdapter(viewAdapter);
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    userReviewCount.setText(String.valueOf(viewAdapter.getItemCount()));
                    activity.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }, 1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceAllComment.addValueEventListener(eventListener);
    }
    public Messenger getCompany(Messenger messenger){
        referenceCompany.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                load();
                ProductBio productBio = snapshot.getValue(ProductBio.class);
                messenger.setEmail(productBio.getCompanyEmail());
                messenger.setName(productBio.getProductName());
                messenger.setUserRef(productBio.getImageRef());
                messenger.setAddress(productBio.getBarCode());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return messenger;
    }

    public Messenger getComment(Messenger messenger){
        referenceComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                load();
                Messenger productBio = snapshot.getValue(Messenger.class);
                messenger.setImageRef(productBio.getImageRef());
                messenger.setComment(productBio.getComment());
                messenger.setRatingBarScore(productBio.getRatingBarScore());
                messenger.setTime(productBio.getTime());
                messenger.setCount(productBio.getCount());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return messenger;
    }

    private void load(){
        activity.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

}
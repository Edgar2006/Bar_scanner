package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qr_scanner.Activity.Login_or_register;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProductPageActivity extends AppCompatActivity {
    private RecyclerView listView;
    private String uploadUri;
    private ImageView imageDataBase;
    private TextView yourName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);
        init();
    }

    public void init(){
        yourName = findViewById(R.id.yourName);
        imageDataBase = findViewById(R.id.profile_image);
        listView = findViewById(R.id.recView);
        listView.setLayoutManager(new LinearLayoutManager(this));
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
                    Intent intent1 = new Intent(ProductPageActivity.this, Login_or_register.class);
                    startActivity(intent1);
                }
                User.NAME = user.getName();
                User.URL = user.getImageRef();
                uploadUri = user.getImageRef();
                yourName.setText(R.string.welcome_home + user.getName());
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


}
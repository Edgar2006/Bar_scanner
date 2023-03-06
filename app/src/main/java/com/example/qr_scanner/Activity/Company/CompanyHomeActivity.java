package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.All.Login_or_register;
import com.example.qr_scanner.Activity.User.Read;
import com.example.qr_scanner.Adapter.ViewAdapterCompany;
import com.example.qr_scanner.Adapter.ViewAdapterCompanyByUser;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.Company;
import com.example.qr_scanner.DataBase_Class.GenRemoteDataSource;
import com.example.qr_scanner.DataBase_Class.ProductBio;
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
import java.util.Objects;

public class CompanyHomeActivity extends AppCompatActivity {
    private ImageView companyImage,setting;
    private TextView companyName,description;
    public RecyclerView listView;
    public RecyclerView.Adapter viewAdapter;
    public ArrayList<ProductBio> listData;
    public RelativeLayout relativeLayout;
    private boolean onlyRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_home);
        addLocalData();
        init();
        readUser();
        getDataFromDataBase();
    }
    private void init(){
        setting = findViewById(R.id.setting);
        description = findViewById(R.id.description);
        relativeLayout = findViewById(R.id.add);
        companyImage = findViewById(R.id.company_image);
        companyName = findViewById(R.id.company_name);
        listView = findViewById(R.id.rec_view);
        listData = new ArrayList<>();
        if (onlyRead){
            viewAdapter = new ViewAdapterCompanyByUser(this,listData);
            relativeLayout.setVisibility(View.GONE);
            setting.setVisibility(View.GONE);
        }
        else{
            viewAdapter = new ViewAdapterCompany(this,listData);
        }
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);

    }
    private void readUser(){
        DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(StaticString.companyInformation).child(User.COMPANY);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Company company = dataSnapshot.getValue(Company.class);
                if(company == null){
                    Intent intent1 = new Intent(CompanyHomeActivity.this, Login_or_register.class);
                    startActivity(intent1);
                }
                User.NAME = company.getName();
                User.URL = company.getImageRef();
                String uploadUri = company.getImageRef();
                companyName.setText(company.getName());
                description.setText(company.getDescription());
                if(!Objects.equals(uploadUri, StaticString.noImage)) {
//                    Picasso.get().load(company.getImageRef()).into(companyImage);
                    Glide.with(CompanyHomeActivity.this).load(company.getImageRef()).into(companyImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myUserRef.addValueEventListener(postListener);
    }
    private void addLocalData(){
        Intent intent = getIntent();
        if (intent != null) {
            String emailToString, passwordToString;
            emailToString = intent.getStringExtra(StaticString.email);
            passwordToString = intent.getStringExtra(StaticString.password);
            onlyRead = intent.getBooleanExtra(StaticString.onlyRead,false);
            String type = StaticString.company;
            if(emailToString!=null && !onlyRead) {
                User.EMAIL = emailToString;
                User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
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
    public void getDataFromDataBase(){
        DatabaseReference referenceProduct = FirebaseDatabase.getInstance().getReference().child(StaticString.productBio);
        GenRemoteDataSource genRemoteDataSource = new GenRemoteDataSource(ProductBio.class);
        genRemoteDataSource.getDataFromDataBase(listView,viewAdapter,listData,referenceProduct);
    }
    public void onClickAdd(View view) {
        Intent intent = new Intent(CompanyHomeActivity.this,CheckBarCodeActivity.class);
        intent.putExtra(StaticString.user,companyName.getText().toString());
        startActivity(intent);
    }
    public void onCLickSetting(View view) {
        Intent intent = new Intent(CompanyHomeActivity.this,CompanyEditActivity.class);
        intent.putExtra(StaticString.email,User.EMAIL_CONVERT);
        startActivity(intent);
    }
}
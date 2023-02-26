package com.example.qr_scanner.Activity.Company;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_scanner.Activity.Login_or_register;
import com.example.qr_scanner.Activity.Read;
import com.example.qr_scanner.Activity.User.HomeActivity;
import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.Adapter.ViewAdapterCompany;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.Company;
import com.example.qr_scanner.DataBase_Class.Messenger;
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
    private ImageView companyImage;
    private TextView companyName;

    public RecyclerView listView;
    public ViewAdapterCompany viewAdapter;
    public ArrayList<ProductBio> listData;
    public RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_home);
        addLocalData();
        init();
        readUser();
        getDataFromDataBase();

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyHomeActivity.this,CheckBarCodeActivity.class);
                intent.putExtra("name",companyName.getText().toString());
                startActivity(intent);
            }
        });

    }
    private void init(){
        relativeLayout = findViewById(R.id.add);
        companyImage = findViewById(R.id.company_image);
        companyName = findViewById(R.id.company_name);
        listView = findViewById(R.id.recView);
        listData = new ArrayList<>();
        viewAdapter = new ViewAdapterCompany(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);

    }
    private void readUser(){
        DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference("Company_Information").child(User.EMAIL_CONVERT);
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
                if(!Objects.equals(uploadUri, "noImage")) {
                    Picasso.get().load(company.getImageRef()).into(companyImage);
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
            String emailToString = intent.getStringExtra("email");
            String passwordToString = intent.getStringExtra("password");
            String type = "Company";
            User.EMAIL = emailToString;
            User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
            try {
                String newUser = emailToString + "\n" + passwordToString + "\n" + type;
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
    public void getDataFromDataBase(){
        DatabaseReference referenceProduct = FirebaseDatabase.getInstance().getReference().child("Product_bio");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    ProductBio productBio = ds.getValue(ProductBio.class);
                    assert  productBio != null;
                    listData.add(productBio);
                }
                listView.setAdapter(viewAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceProduct.addValueEventListener(eventListener);

    }
}
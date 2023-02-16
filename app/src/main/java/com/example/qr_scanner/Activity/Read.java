package com.example.qr_scanner.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.qr_scanner.Class.LexicographicComparator;
import com.example.qr_scanner.Class.TimeComparator;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Read extends AppCompatActivity {
    private boolean sortMethod;
    private RecyclerView listView;
    private ViewAdapter viewAdapter;
    private ArrayList<Messenger> listData;
    private String bareCode;
    private DatabaseReference referenceComment,referenceProduct;
    private String shortText,longText;
    private TextView productName, bioText,showMore;
    private ImageView productImageView;
    private boolean ifMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        init();
        getDataFromDataBase();
        try {
            getDataProductDataBase();

        }catch (Exception e){
            Log.d("F",e.toString());
        }
    }
    private void init(){
        sortMethod = false;
        listView = findViewById(R.id.recView);
        listData = new ArrayList<Messenger>();
        viewAdapter = new ViewAdapter(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);
        Intent intent = getIntent();
        if(intent!=null){
            bareCode = intent.getStringExtra("bareCode");
        }
        referenceComment = FirebaseDatabase.getInstance().getReference("Product").child(bareCode);
        referenceProduct = FirebaseDatabase.getInstance().getReference("Product_bio").child(bareCode);
        productName = findViewById(R.id.productName);
        bioText = findViewById(R.id.bioShort);
        productImageView = findViewById(R.id.productImageView);
        showMore = findViewById(R.id.showMore);
        ifMore = true;
    }

    public void onClickShowMore(View view){
        if(ifMore){
            bioText.setText(longText);
            ifMore=!ifMore;
            showMore.setText("close");
        }
        else{
            bioText.setText(shortText);
            ifMore=!ifMore;
            showMore.setText("show more");
        }
    }
    public void onClickComment(View view){
        Intent intent = new Intent(Read.this,NewCommentActivity.class);
        intent.putExtra("barCode", bareCode);
        startActivity(intent);
    }
    public void onClickEditProduct(View view){
        Intent intent = new Intent(Read.this, Product_activity.class);
        intent.putExtra("barCode", bareCode);
        startActivity(intent);
    }
    public void onClickSortByLike(View view){
        sortMethod=true;
        sortMethod();
    }
    public void onClickSortByTime(View view){
        sortMethod=false;
        sortMethod();
    }
    public void sortMethod(){
        if(sortMethod) {
            Collections.sort(listData, new LexicographicComparator());
            listView.setAdapter(viewAdapter);
        }
        else{
            Collections.sort(listData, new TimeComparator());
            listView.setAdapter(viewAdapter);
        }
    }

    private void getDataFromDataBase(){

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    Messenger messenger = ds.getValue(Messenger.class);
                    assert  messenger != null;
                    listData.add(messenger);
                }
                sortMethod();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceComment.addValueEventListener(eventListener);

    }
    private void getDataProductDataBase(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    ProductBio productBio = snapshot.getValue(ProductBio.class);
                    productName.setText(productBio.getProductName());
                    longText = productBio.getBioLong();
                    shortText = productBio.getBioShort();
                    bioText.setText(shortText);
                    if(!Objects.equals(productBio.getImageRef(), "noImage")) {
                        Picasso.get().load(productBio.getImageRef()).into(productImageView);
                    }
                }
                catch (Exception e){
                    Log.d("F",e.toString());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceProduct.addValueEventListener(eventListener);

    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu,menu);
        return true;
    }

}
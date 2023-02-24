package com.example.qr_scanner.Activity;


import static com.google.common.primitives.UnsignedInts.max;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_scanner.Activity.Company.Product_activityBioEdit;
import com.example.qr_scanner.Activity.User.NewCommentActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.LexicographicComparator;
import com.example.qr_scanner.Class.TimeComparator;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.StatisticsProduct;
import com.example.qr_scanner.DataBase_Class.User;
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
    private TextView productName,bioText,showMore,ratingBarScore,companyName;
    private ImageView productImageView,companyImageView;
    private boolean ifMore;
    private RelativeLayout relativeLayout;
    private DatabaseReference productNotification;
    private RatingBar ratingBar;
    public static float SUM;
    public static int COUNT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        init();
        getDataFromDataBase();
        getDataProductDataBase();
        writeCountPeople();
    }



    private void init(){
        Read.SUM = 0;
        Read.COUNT = 0;
        companyImageView = findViewById(R.id.company_image);
        companyName = findViewById(R.id.companyName);
        ratingBarScore = findViewById(R.id.ratingBarScore);
        ratingBar = findViewById(R.id.ratingBar);
        StatisticsProduct.COUNT=0;
        StatisticsProduct.COUNT_BUY_PEOPLE=0;
        relativeLayout = findViewById(R.id.bio);
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

        productNotification = FirebaseDatabase.getInstance().getReference("Company_statistics").child(bareCode);
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
        Intent intent = new Intent(Read.this, NewCommentActivity.class);
        intent.putExtra("barCode", bareCode);
        startActivity(intent);
    }
    public void onClickEditProduct(View view){
        Intent intent = new Intent(Read.this, Product_activityBioEdit.class);
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
        Read.SUM = 0;
        Read.COUNT = 0;
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                    Read.SUM = 0;
                    Read.COUNT = 0;
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    Messenger messenger = ds.getValue(Messenger.class);
                    assert  messenger != null;
                    Messenger a = getCommentUserNameAndUserImage(messenger);
                    listData.add(a);
                    Read.SUM += messenger.getRatingBarScore();
                    Read.COUNT++;
                }
                sortMethod();
                float a = Read.SUM / Read.COUNT;
                ratingBar.setRating(a);
                ratingBarScore.setText(String.valueOf(a));
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
                    companyName.setText(productBio.getCompanyName());
                    productName.setText(productBio.getProductName());
                    longText = productBio.getBioLong();
                    shortText = productBio.getBioShort();
                    bioText.setText(shortText);
                    if(!Objects.equals(productBio.getImageRef(), "noImage")) {
                        Picasso.get().load(productBio.getImageRef()).into(productImageView);
                    }
                    if(!Objects.equals(productBio.getCompanyRef(), "noImage")) {
                        Picasso.get().load(productBio.getCompanyRef()).into(companyImageView);
                    }
                }
                catch (Exception e){
                    relativeLayout.setVisibility(View.GONE);

                    Log.d("F",e.toString());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                relativeLayout.setVisibility(View.GONE);
            }
        };
        referenceProduct.addValueEventListener(eventListener);

    }

    private Messenger getCommentUserNameAndUserImage(Messenger messenger){
        Messenger a = messenger;
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("User").child(Function.convertor(a.getEmail()));
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                a.setUserRef(user.getImageRef());
                a.setName(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        referenceUser.addValueEventListener(eventListener);
        return  a;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu,menu);
        return true;
    }



    private void writeCountPeople(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StatisticsProduct unit = snapshot.getValue(StatisticsProduct.class);
                assert unit != null;
                StatisticsProduct.COUNT = max(unit.getCount(),StatisticsProduct.COUNT);
                StatisticsProduct.COUNT_BUY_PEOPLE = unit.getCountBuyPeople();
                Log.d("________________________" +System.currentTimeMillis() +" ____" , String.valueOf(StatisticsProduct.COUNT));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Read.this, "@", Toast.LENGTH_SHORT).show();
            }
        };
        productNotification.addValueEventListener(eventListener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("________________________@@" +System.currentTimeMillis() +" ____" , String.valueOf(StatisticsProduct.COUNT));

                StatisticsProduct.COUNT++;
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Company_statistics").child(bareCode);
                databaseReference.setValue(new StatisticsProduct(StatisticsProduct.COUNT,StatisticsProduct.COUNT_BUY_PEOPLE));

            }
        }, 1 * 1000);

    }

    public void onClickProduct(View view) {
        //Intent intent = new Intent(Read.this,);
    }
}
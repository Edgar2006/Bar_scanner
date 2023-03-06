package com.example.qr_scanner.Activity.User;


import static android.widget.Toast.*;
import static com.google.common.primitives.UnsignedInts.max;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.Company.CompanyHomeActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.LexicographicComparator;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.Rating;
import com.example.qr_scanner.Class.TimeComparator;
import com.example.qr_scanner.DataBase_Class.Company;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.Adapter.ViewAdapter;
import com.example.qr_scanner.DataBase_Class.ProductBio;
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
    private String barCode;
    private DatabaseReference referenceComment,referenceProduct;
    private String shortText,longText;
    private TextView productName,bioText,showMore,ratingBarScore,companyName;
    private ImageView productImageView,companyImageView;
    private boolean ifMore;
    private RelativeLayout relativeLayout;
    private DatabaseReference productRating;
    private RatingBar ratingBar;
    private Rating rating;
    private Boolean ifYouHaveComment;

    private DatabaseReference companyNameRef;

    private DatabaseReference referenceHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        init();
        getDataFromDataBase();
        getDataProductDataBase();
        writeCountPeople();
        ratingBar.setOnRatingBarChangeListener((ratingBar, ratingValue, fromUser) -> {
            if(fromUser) {
                sendToData();
            }
        });
    }
    public void sendToData(){
        if(ifYouHaveComment){
            ratingBar.setIsIndicator(true);
            Messenger messenger;
            messenger = new Messenger(User.EMAIL, User.NAME, StaticString.haveARating, barCode, "0",StaticString.noImage,StaticString.noImage,System.currentTimeMillis(),ratingBar.getRating());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.product).child(barCode).child(User.EMAIL_CONVERT);
            reference.setValue(messenger);
        }
        else{
            ratingBar.setRating(rating.rating / rating.countRating);
            makeText(this, R.string.yourGiveyourRating, LENGTH_SHORT).show();
            ratingBar.setIsIndicator(true);
        }
    }


    private void init(){
        referenceHistory = FirebaseDatabase.getInstance().getReference(StaticString.history).child(User.EMAIL_CONVERT);
        ifYouHaveComment = true;
        companyNameRef = FirebaseDatabase.getInstance().getReference(StaticString.companyInformation);
        rating = new Rating(0,0,0);
        companyImageView = findViewById(R.id.company_image);
        companyName = findViewById(R.id.company_name);
        ratingBarScore = findViewById(R.id.rating_bar_score);
        ratingBar = findViewById(R.id.rating_bar);
        relativeLayout = findViewById(R.id.bio);
        sortMethod = false;
        listView = findViewById(R.id.rec_view);
        listData = new ArrayList<Messenger>();
        viewAdapter = new ViewAdapter(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);
        Intent intent = getIntent();
        if(intent!=null){
            barCode = intent.getStringExtra(StaticString.barCode);
            if (Objects.equals(intent.getStringExtra(StaticString.type), StaticString.company)){ifYouHaveComment = false;}
        }
        productRating = FirebaseDatabase.getInstance().getReference(StaticString.productRating).child(barCode);
        referenceComment = FirebaseDatabase.getInstance().getReference(StaticString.product).child(barCode);
        referenceProduct = FirebaseDatabase.getInstance().getReference(StaticString.productBio).child(barCode);
        productName = findViewById(R.id.product_name);
        bioText = findViewById(R.id.bioShort);
        productImageView = findViewById(R.id.product_image_view);
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
        intent.putExtra(StaticString.barCode, barCode);
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
                    rating.rating = 0;
                    rating.countRating = 0;
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    Messenger messenger = ds.getValue(Messenger.class);
                    assert  messenger != null;
                    Messenger a = getCommentUserNameAndUserImage(messenger);
                    if(!Objects.equals(a.getComment(), StaticString.haveARating)) {
                        listData.add(a);
                    }
                    if(Objects.equals(Function.convertor(a.getEmail()), User.EMAIL_CONVERT)) {
                        ifYouHaveComment = false;
                    }
                    rating.rating += messenger.getRatingBarScore();
                    rating.countRating++;
                }
                sortMethod();
                float rating_ =  rating.rating / rating.countRating;
                if(rating.countRating == 0){
                    rating_=0F;
                }
                ratingBar.setRating(rating_);
                ratingBarScore.setText(Float.toString(rating_) + "  (" + rating.countRating + ')');
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
                    if(!Objects.equals(productBio.getImageRef(), StaticString.noImage)) {
                        Glide.with(Read.this).load(productBio.getImageRef()).into(productImageView);
                    }
                    referenceHistory.child(barCode).setValue(productBio);
                    DatabaseReference reference = companyNameRef.child(productBio.getCompanyEmail());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Company companyNameAndImageUnit = snapshot.getValue(Company.class);
                            companyName.setText(companyNameAndImageUnit.getName());
                            User.COMPANY = companyNameAndImageUnit.getEmail();
                            if(!Objects.equals(companyNameAndImageUnit.getImageRef(), StaticString.noImage)) {
                                Glide.with(Read.this).load(companyNameAndImageUnit.getImageRef()).into(companyImageView);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                catch (Exception e){
                    relativeLayout.setVisibility(View.GONE);
                    ProductBio productBio = new ProductBio("","",barCode,StaticString.noImage, StaticString.noImage,"","",barCode);
                    referenceHistory.child(barCode).setValue(productBio);
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
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference(StaticString.user).child(Function.convertor(a.getEmail()));
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
    private void writeCountPeople(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Rating unit = snapshot.getValue(Rating.class);
                try {
                    rating.countView = max(unit.countView,rating.countView);
                }catch (Exception e){}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        productRating.addValueEventListener(eventListener);
        new Handler().postDelayed(() -> {
            rating.countView++;
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(StaticString.productRating).child(barCode);
            databaseReference.setValue(rating);
        }, 1 * 1000);

    }

    public void onClickProduct(View view) {
        Intent intent = new Intent(Read.this, CompanyHomeActivity.class);
        intent.putExtra(StaticString.onlyRead,true);
        intent.putExtra(StaticString.email,User.COMPANY);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu,menu);
        return true;
    }
}
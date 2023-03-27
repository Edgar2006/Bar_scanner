package com.example.qr_scanner.Activity.User;


import static android.widget.Toast.*;

import static java.lang.Integer.max;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.Company.CompanyHomeActivity;
import com.example.qr_scanner.Activity.Company.Product_activityBioEdit;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.Translations;
import com.example.qr_scanner.Class.UserLike;
import com.example.qr_scanner.Class.noActivThisTIme.LexicographicComparator;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.MyBool;
import com.example.qr_scanner.DataBase_Class.Rating;
import com.example.qr_scanner.Class.noActivThisTIme.TimeComparator;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class Read extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private boolean sortMethod,ifYouHaveComment,ifMore;
    private RecyclerView listView;
    private ViewAdapter viewAdapter;
    private ArrayList<Messenger> listData;
    private ArrayList<UserLike> userLikeArrayList;
    private DatabaseReference referenceComment,referenceProduct,companyNameRef,referenceHistory,productRating;
    private String shortText,longText,barCode;
    private TextView productName,bioText,showMore,ratingBarScore,companyName,barCodeTextView,firstComment,sortByText;
    private ImageView productImageView,companyImageView;
    private RelativeLayout relativeLayout,companyCorrect,viewLayoutComment;
    private RelativeLayout userCorrect;
    private RatingBar ratingBar;
    private Rating rating;
    private RelativeLayout activity;
    private ProgressBar progressBar;
    private Button buttonAppBar;
    private Messenger ifYouHaveAComment;

    private TextView translateView;
    private ProgressDialog progressDialog;
    private String sourceLanguageCode = "en";
    private String destinationLanguageCode = "ur";
    private String sourceLanguageText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        init();
        load(true);
        getDataFromDataBase();
        getDataProductDataBase();
        writeCountPeople();
        if(viewAdapter.getItemCount()==0){
            viewLayoutComment.setVisibility(View.GONE);
        }
        else{
            viewLayoutComment.setVisibility(View.VISIBLE);
        }
        ratingBar.setOnRatingBarChangeListener((ratingBar, ratingValue, fromUser) -> {
            if(fromUser) {
                sendToData(ratingValue);
            }
        });
    }

    private void init(){
        userLikeArrayList = new ArrayList<>();
        sortByText = findViewById(R.id.sortByText);
        viewLayoutComment = findViewById(R.id.view_comment);
        viewLayoutComment.setVisibility(View.GONE);
        firstComment = findViewById(R.id.first_comment);
        firstComment.setVisibility(View.VISIBLE);
        activity = findViewById(R.id.activity);
        progressBar = findViewById(R.id.progress_bar);
        barCodeTextView = findViewById(R.id.product_barCode);
        ifYouHaveAComment = new Messenger(StaticString.haveARating);
        buttonAppBar = findViewById(R.id.button_app_bar);
        companyCorrect = findViewById(R.id.company_correct);
        userCorrect = findViewById(R.id.user_correct);
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
        listData = new ArrayList<>();
        viewAdapter = new ViewAdapter(this,listData);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(viewAdapter);
        Intent intent = getIntent();
        if(intent!=null){
            barCode = intent.getStringExtra(StaticString.barCode);
            if (User.ifCompany){ifYouHaveComment = false;buttonAppBar.setVisibility(View.GONE);}
        }
        productRating = FirebaseDatabase.getInstance().getReference(StaticString.productRating).child(barCode);
        referenceComment = FirebaseDatabase.getInstance().getReference(StaticString.product).child(barCode);
        referenceProduct = FirebaseDatabase.getInstance().getReference(StaticString.productBio).child(barCode);
        productName = findViewById(R.id.product_name);
        bioText = findViewById(R.id.bioShort);
        productImageView = findViewById(R.id.product_image_view);
        showMore = findViewById(R.id.show_more);
        ifMore = true;

        progressDialog = new ProgressDialog(Read.this);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setCanceledOnTouchOutside(false);
        translateView = findViewById(R.id.translate);

    }

    public void sendToData(Float ratingValue){
        if(ifYouHaveComment){
            Messenger messenger;
            messenger = new Messenger(User.EMAIL, User.NAME, StaticString.haveARating, barCode, "0",StaticString.noImage,StaticString.noImage,System.currentTimeMillis(),ratingBar.getRating());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.product).child(barCode).child(User.EMAIL_CONVERT);
            if(ifYouHaveAComment.getTime() != 0){
                messenger = ifYouHaveAComment;
                rating.countRating--;
                rating.rating-=messenger.getRatingBarScore();
                messenger.setRatingBarScore(ratingValue);
            }
            reference.setValue(messenger);
        }
        else{
            ratingBar.setRating(rating.rating / rating.countRating);
            makeText(this, R.string.yourGiveyourRating, LENGTH_SHORT).show();
            ratingBar.setIsIndicator(true);
        }
    }
    public void onClickShowMore(View view){
        if(ifMore){
            bioText.setText(longText);
            ifMore=!ifMore;
            showMore.setText(R.string.close);
            if (!Objects.equals(sourceLanguageText, bioText.getText().toString())){
                onClickTranslate(view);
            }
        }
        else{
            bioText.setText(shortText);
            ifMore=!ifMore;
            showMore.setText(R.string.show_more);
            if (!Objects.equals(sourceLanguageText, bioText.getText().toString())){
                onClickTranslate(view);
            }
        }
    }
    public void onClickComment(View view){
        Intent intent = new Intent(Read.this, NewCommentActivity.class);
        intent.putExtra(StaticString.barCode, barCode);
        startActivity(intent);
    }
    public void SortByLike(){
        sortMethod=true;
        sortMethod();
        sortByText.setText(R.string.Sort_review_most_liked);
    }
    public void SortByTime(){
        sortMethod=false;
        sortMethod();
        sortByText.setText(R.string.sort_by_time);
    }
    public void onClickSort(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option_1:
                SortByLike();
                return true;
            case R.id.option_2:
                SortByTime();
                return true;
            default:
                return false;

        }
    }
    public void sortMethod(){
        if(sortMethod) {
            sortLike();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                sortLike();
            }, 310);

        }
        else{
            Collections.sort(listData, new TimeComparator());
            listView.setAdapter(viewAdapter);
        }
    }
    public void sortLike(){
        setUserLikeArrayList();
        userLikeArrayList.sort((a, b) -> Integer.compare(b.getLike(), a.getLike()));
        ArrayList<Messenger> messengerArrayList = listData;
        ArrayList<Messenger> newListData = new ArrayList<>();
        for(int i=0;i<userLikeArrayList.size();i++){
            for(int j=0;j<messengerArrayList.size();j++){
                if(Objects.equals(userLikeArrayList.get(i).getUser(), Function.CONVERTOR(messengerArrayList.get(j).getEmail()))){
                    newListData.add(messengerArrayList.get(j));
                    listData.get(j).setCount(String.valueOf(userLikeArrayList.get(i).getLike()));
                }
            }
        }
        Log.e("_",listData.toString());
        Collections.sort(listData, new LexicographicComparator());
        listView.setAdapter(viewAdapter);
    }
    private void setUserLikeArrayList(){
        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference(StaticString.friends).child(barCode);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot data: snapshot.getChildren()) {
                    int a=0;
                    for(DataSnapshot data1: data.getChildren()) {
                        try {
                            MyBool isLike = data1.getValue(MyBool.class);
                            boolean isOk = isLike.isLike();
                            if (isOk) {
                                a++;
                            }
                        } catch (Exception e) {
                        }
                    }
                    Log.e("_", String.valueOf(a));
                    Log.e("_", String.valueOf(data.getRef()));
                    userLikeArrayList.add(new UserLike(a,data.getKey()));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        likeReference.addValueEventListener(eventListener);

    }
    private void getDataFromDataBase(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listData.size() > 0){
                    listData.clear();
                    rating.rating = 0;
                    rating.countRating = 0;
                    userLikeArrayList.clear();
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    Messenger messenger = ds.getValue(Messenger.class);
                    assert  messenger != null;
                    Messenger a = getCommentUserNameAndUserImage(messenger);
                    if(!Objects.equals(a.getComment(), StaticString.haveARating)) {
                        listData.add(a);
                    }
                    if(Objects.equals(Function.CONVERTOR(a.getEmail()), User.EMAIL_CONVERT)) {
                        ifYouHaveAComment = a;
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
                load(false);
                if(viewAdapter.getItemCount()==0){
                    viewLayoutComment.setVisibility(View.GONE);
                }
                else{
                    viewLayoutComment.setVisibility(View.VISIBLE);
                    firstComment.setVisibility(View.GONE);
                }
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
                    User.COMPANY_NAME = productBio.getCompanyName();
                    User.COMPANY_EMAIL = productBio.getCompanyEmail();
                    User.COMPANY_URL = productBio.getImageRef();
                    barCodeTextView.setText(barCode);
                    companyName.setText(productBio.getCompanyName());
                    productName.setText(productBio.getProductName());
                    longText = productBio.getBioLong();
                    shortText = productBio.getBioShort();
                    bioText.setText(shortText);
                    if (longText.length() <= 200){showMore.setVisibility(View.GONE);}
                    if(!Objects.equals(productBio.getImageRef(), StaticString.noImage)) {
                        Glide.with(getApplicationContext()).load(productBio.getImageRef()).into(productImageView);
                    }
                    sourceLanguageText = longText;
                    productBio.setCompanyRef(Long.toString(System.currentTimeMillis()));
                    referenceHistory.child(barCode).setValue(productBio);
                    if(!productBio.getAccess()){
                        userCorrect.setVisibility(View.VISIBLE);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.user).child(productBio.getCompanyEmail());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User userNameAndImageUnit = snapshot.getValue(User.class);
                                companyName.setText(userNameAndImageUnit.getName());
                                User.COMPANY_EMAIL = userNameAndImageUnit.getEmail();
                                User.COMPANY_URL = userNameAndImageUnit.getImageRef();
                                if(!Objects.equals(userNameAndImageUnit.getImageRef(), StaticString.noImage)) {
                                    Glide.with(getApplicationContext()).load(userNameAndImageUnit.getImageRef()).into(companyImageView);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else{
                        companyCorrect.setVisibility(View.VISIBLE);
                        DatabaseReference reference = companyNameRef.child(productBio.getCompanyEmail());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Company companyNameAndImageUnit = snapshot.getValue(Company.class);
                                companyName.setText(companyNameAndImageUnit.getName());
                                User.COMPANY_EMAIL = companyNameAndImageUnit.getEmail();
                                User.COMPANY_URL = companyNameAndImageUnit.getImageRef();
                                if(!Objects.equals(companyNameAndImageUnit.getImageRef(), StaticString.noImage)) {
                                    Glide.with(getApplicationContext()).load(companyNameAndImageUnit.getImageRef()).into(companyImageView);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }


                }
                catch (Exception e){
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        activity.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }, 110);
                    firstBio();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                relativeLayout.setVisibility(View.GONE);
            }
        };
        referenceProduct.addValueEventListener(eventListener);

    }
    public void firstBio() {
        String title = getString(R.string.no_dataBase_this_product);
        String message = getString(R.string.you_can_add);
        String button1String = getString(R.string.add_now);
        String button2String = getString(R.string.go_back);
        AlertDialog.Builder builder = new AlertDialog.Builder(Read.this);
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение
        builder.setPositiveButton(button1String, (dialog, id) -> {
            Intent intent = new Intent(Read.this, Product_activityBioEdit.class);
            intent.putExtra(StaticString.barCode,barCode);
            intent.putExtra(StaticString.haveARating,StaticString.haveARating);
            startActivity(intent);
        });
        builder.setNegativeButton(button2String, (dialog, id) -> finish());
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }
    private Messenger getCommentUserNameAndUserImage(Messenger messenger){
        Messenger a = messenger;
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference(StaticString.user).child(Function.CONVERTOR(a.getEmail()));
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
        if (companyCorrect.getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(Read.this, CompanyHomeActivity.class);
            intent.putExtra(StaticString.onlyRead,true);
            intent.putExtra(StaticString.email,User.COMPANY_EMAIL);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(Read.this, UserAllCommentShowActivity.class);
            intent.putExtra(StaticString.email,User.COMPANY_EMAIL);
            intent.putExtra(StaticString.user,User.COMPANY_NAME);
            intent.putExtra(StaticString.userImage,User.COMPANY_URL);
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu,menu);
        return true;
    }
    private void load(boolean b){
        if(b){
            activity.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            if (buttonAppBar.getVisibility() != View.GONE){
                buttonAppBar.setVisibility(View.VISIBLE);
            }
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                activity.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }, 100);
        }
    }

    public void onClickTranslate(View view) {
        if (!ifMore){sourceLanguageText = longText;}
        else{sourceLanguageText = shortText;}
        Log.e("1_", String.valueOf(System.currentTimeMillis()));
        Translations translations = new Translations(progressDialog,sourceLanguageCode,destinationLanguageCode,sourceLanguageText,bioText,translateView,view);
    }

    @Override
    public void onBackPressed() {
        Intent a;
        if (User.ifCompany){
            a = new Intent(this, CompanyHomeActivity.class);
            a.putExtra(StaticString.onlyRead, false);
            a.putExtra(StaticString.email, User.COMPANY);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
        }
        else{
            if (User.FINISH_ACTIVITY) {
                a = new Intent(this, HomeActivity.class);
                a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
            }
            else{
                finish();
            }
        }

    }

    public void onClickUserCorrect(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.added_maybe_no_correct_info)
                .setMessage(R.string.so_no_correct)
                .setPositiveButton( R.string.close, (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
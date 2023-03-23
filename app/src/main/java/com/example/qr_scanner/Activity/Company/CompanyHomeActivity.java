package com.example.qr_scanner.Activity.Company;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.All.Login_or_register;
import com.example.qr_scanner.Activity.User.HomeActivity;
import com.example.qr_scanner.Adapter.ViewAdapterCompany;
import com.example.qr_scanner.Adapter.ViewAdapterCompanyByUser;
import com.example.qr_scanner.Class.AppCompat;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.LanguageManger;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.Class.Translations;
import com.example.qr_scanner.DataBase_Class.Company;
import com.example.qr_scanner.DataBase_Class.GenRemoteDataSource;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CompanyHomeActivity extends AppCompat implements PopupMenu.OnMenuItemClickListener{
    private ImageView companyImage,setting;
    private TextView companyName,description;
    public RecyclerView listView;
    public RecyclerView.Adapter viewAdapter;
    public ArrayList<ProductBio> listData;
    public RelativeLayout relativeLayout;
    private boolean onlyRead;
    private RelativeLayout activity;
    private ProgressBar progressBar;

    private TextView translateView;
    private ProgressDialog progressDialog;
    private String sourceLanguageCode = "en";
    private String destinationLanguageCode = "ur";
    private String sourceLanguageText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_home);
        Log.e("_______________","_!!");
        addLocalData();
        init();
        load(true);
        readUser();
        getDataFromDataBase();

    }
    private void init(){
        User.PAGE = true;
        translateView = findViewById(R.id.translate);
        progressDialog = new ProgressDialog(CompanyHomeActivity.this);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setCanceledOnTouchOutside(false);
        setting = findViewById(R.id.setting);
        description = findViewById(R.id.description);
        relativeLayout = findViewById(R.id.add);
        companyImage = findViewById(R.id.company_image);
        companyName = findViewById(R.id.company_name);
        listView = findViewById(R.id.rec_view);
        activity = findViewById(R.id.activity);
        progressBar = findViewById(R.id.progress_bar);
        listData = new ArrayList<>();
        if (onlyRead){
            Log.e("_","1");
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
        DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference(StaticString.companyInformation).child(User.COMPANY_EMAIL);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Company company = dataSnapshot.getValue(Company.class);
                if(company == null){
                    Intent intent1 = new Intent(CompanyHomeActivity.this, Login_or_register.class);
                    startActivity(intent1);
                }
                Log.e("____",myUserRef.getKey());
                User.NAME = company.getName();
                User.URL = company.getImageRef();
                User.DESCRIPTION = company.getDescription();
                sourceLanguageText = company.getDescription();
                String uploadUri = company.getImageRef();
                companyName.setText(company.getName());
                description.setText(company.getDescription());
                if(!Objects.equals(uploadUri, StaticString.noImage)) {
                    Glide.with(getApplicationContext()).load(company.getImageRef()).into(companyImage);
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
        if (intent != null && !onlyRead) {
            String emailToString, passwordToString;
            emailToString = intent.getStringExtra(StaticString.email);
            User.COMPANY_EMAIL = Function.CONVERTOR(emailToString);
            passwordToString = intent.getStringExtra(StaticString.password);
            onlyRead = intent.getBooleanExtra(StaticString.onlyRead,false);
            Log.e("___", String.valueOf(onlyRead));
            String type = StaticString.company;
            if(emailToString!=null && !onlyRead) {
                User.EMAIL = emailToString;
                User.EMAIL_CONVERT = Function.CONVERTOR(User.EMAIL);
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
            genRemoteDataSource.getDataFromDataBase(listView, viewAdapter, listData, referenceProduct, User.COMPANY_EMAIL, activity, progressBar);
    }
    public void onClickAdd(View view) {
        Intent intent = new Intent(CompanyHomeActivity.this,CheckBarCodeActivity.class);
        intent.putExtra(StaticString.user,Function.POP(companyName.getText().toString()));
        startActivity(intent);
    }




    private void load(boolean b){
        if(b){
            activity.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                activity.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }, 1000);
        }
    }

    public void onCLickMore(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.setting_manu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_profile:
                onCLickSetting();
                return true;
            case R.id.change_language:
                onClickChangeLanguage();
                return true;
            case R.id.logout:
                onClickLogout();
                return true;
            default:
                return false;

        }
    }
    public void onClickChangeLanguage(){
        LanguageManger languageManger = new LanguageManger(this);
        String destinationLanguageCode = languageManger.getLang();
        String[] listItems = new String[]{(String) getText(R.string.english), (String) getText(R.string.russian), (String) getText(R.string.armenia)};
        String[] language = new String[]{"en", "ru", "am"};
        int index=0;
        for(int i=0;i<language.length;i++){
            if(language[i].equals(destinationLanguageCode)){
                index=i;
                break;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CompanyHomeActivity.this);
        builder.setTitle(R.string.chosse_an_item);
        builder.setIcon(R.drawable.ic_baseline_language_24);
        builder.setSingleChoiceItems(listItems, index, (dialog, i) -> {
            languageManger.updateResource(language[i]);
            recreate();
            dialog.dismiss();
        });
        builder.setNeutralButton(R.string.cancel, (dialog, i) -> {

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onCLickSetting() {
        Intent intent = new Intent(CompanyHomeActivity.this,CompanyEditActivity.class);
        intent.putExtra(StaticString.email,User.EMAIL_CONVERT);
        intent.putExtra(StaticString.user,new User(companyName.toString(),User.EMAIL_CONVERT,"0",false));
        startActivity(intent);
    }
    public void onClickLogout() {
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        try {
            String temp = "";
            FileOutputStream fileOutputStream = openFileOutput(StaticString.Authentication, MODE_PRIVATE);
            fileOutputStream.write(temp.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        firebaseAuth.signOut();
        Intent intent = new Intent(CompanyHomeActivity.this, Login_or_register.class);
        startActivity(intent);
    }

    public void onClickTranslate(View view) {
        Translations translations = new Translations(progressDialog,sourceLanguageCode,destinationLanguageCode,sourceLanguageText,description,translateView,view);
    }

    @Override
    public void onBackPressed() {
        if (onlyRead){
            finish();
        }
        else{
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

    }
}
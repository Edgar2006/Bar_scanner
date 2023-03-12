package com.example.qr_scanner.Activity.Company;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.All.Login_or_register;
import com.example.qr_scanner.Activity.User.SettingsActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.Company;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CompanyEditActivity extends AppCompatActivity {
    private TextInputLayout name,description;
    private ImageView companyImage;
    private StorageReference storageRef;
    private Uri uploadUri;
    private RelativeLayout activity;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_edit);
        init();
        load(true);
    }
    private void init(){
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        companyImage = findViewById(R.id.company_image);
        storageRef = FirebaseStorage.getInstance().getReference(StaticString.imageCompanyLogo);
        name.getEditText().setText(User.NAME);
        description.getEditText().setText(User.DESCRIPTION);
        uploadUri = Uri.parse(User.URL);
        Glide.with(CompanyEditActivity.this).load(User.URL).into(companyImage);

    }



    public void sendToData(){
        Intent dataGet = getIntent();
        String emailToString, passwordToString;
        emailToString = Function.CONVERTOR(dataGet.getStringExtra(StaticString.email));
        passwordToString = dataGet.getStringExtra(StaticString.password);
        User user = new User("","","",false);
        try {
            user = (User)dataGet.getSerializableExtra(StaticString.user);
            user.setImageRef("0");
        }catch (Exception e){

        }
        DatabaseReference boolRef = FirebaseDatabase.getInstance().getReference(StaticString.company).child(emailToString);
        boolRef.setValue(user);

        Company company;
        company = new Company(emailToString, Function.POP(name.getEditText().getText().toString()), Function.POP(description.getEditText().getText().toString()), StaticString.noImage);
        if(uploadUri != null){
            company.setImageRef(uploadUri.toString());
        }
        User.COMPANY = Function.CONVERTOR(emailToString);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.companyInformation).child(emailToString);
        reference.setValue(company);
        Toast.makeText(CompanyEditActivity.this, "Your comment send", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CompanyEditActivity.this,CompanyHomeActivity.class);
        intent.putExtra(StaticString.email,emailToString);
        intent.putExtra(StaticString.password,passwordToString);
        startActivity(intent);
    }

    private void uploadImage(){
        try {
            Bitmap bitmap = ((BitmapDrawable) companyImage.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            StorageReference mRef = storageRef.child(User.EMAIL_CONVERT + "_");
            final UploadTask uploadTask = mRef.putBytes(byteArray);
            Task<Uri> task = uploadTask.continueWithTask(task1 -> mRef.getDownloadUrl()).addOnCompleteListener(task12 -> {
                uploadUri = task12.getResult();
                Toast.makeText(CompanyEditActivity.this, "Loading is complete", Toast.LENGTH_SHORT).show();
                sendToData();
            });
        }catch (Exception e){
            sendToData();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null){
            if(resultCode == RESULT_OK){
                companyImage.setImageURI(data.getData());
            }
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null) {
                Toast.makeText(this, result.getContents().toString(), Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }

    }
    public void onClickLogout(View view) {
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
        Intent intent = new Intent(CompanyEditActivity.this, Login_or_register.class);
        startActivity(intent);
    }
    public void onClickChooseImage(View view){
        getImage();
    }
    public void onClickSubmit(View view){
        uploadImage();load(false);
    }
    private void getImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    private void load(boolean b){
        if(b){
            activity = findViewById(R.id.activity);
            activity.setVisibility(View.VISIBLE);
            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                activity.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }, 100);
        }
    }
}
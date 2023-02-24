package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_scanner.Activity.User.NewCommentActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.Company;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CompanyEditActivity extends AppCompatActivity {
    private TextInputLayout name,description;
    private ImageView companyImage;
    private StorageReference storageRef;
    private Uri uploadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_edit);
        init();
    }
    private void init(){
        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        companyImage = findViewById(R.id.company_image);
        storageRef = FirebaseStorage.getInstance().getReference("ImageCompanyLogo");
    }



    public void onClickSubmit(View view){
        uploadImage();
    }

    public void onClickChooseImage(View view){
        getImage();
    }

    private void uploadImage(){
        try {
            Bitmap bitmap = ((BitmapDrawable) companyImage.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            StorageReference mRef = storageRef.child(User.EMAIL_CONVERT + "_" + name.getEditText().getText() + "my_image");
            final UploadTask uploadTask = mRef.putBytes(byteArray);
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    uploadUri = task.getResult();
                    Toast.makeText(CompanyEditActivity.this, "Loading is complete", Toast.LENGTH_SHORT).show();
                    sendToData();
                }
            });
        }catch (Exception e){
            sendToData();
        }

    }
    public void sendToData(){
        Intent dataGet = getIntent();
        String emailToString = Function.convertor(dataGet.getStringExtra("email"));
        String passwordToString = dataGet.getStringExtra("password");

        Company company;
        company = new Company(emailToString, name.getEditText().getText().toString(), description.getEditText().getText().toString(), "noImage");
        if(uploadUri != null){
            company.setImageRef(uploadUri.toString());
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Company_Information").child(emailToString);
        reference.setValue(company);
        Toast.makeText(CompanyEditActivity.this, "Your comment send", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CompanyEditActivity.this,CompanyHomeActivity.class);
        intent.putExtra("email",emailToString);
        intent.putExtra("password",passwordToString);
        startActivity(intent);
    }

    private void getImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
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




}
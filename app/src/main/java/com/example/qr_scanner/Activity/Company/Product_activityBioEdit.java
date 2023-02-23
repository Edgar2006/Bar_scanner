package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;

public class Product_activityBioEdit extends AppCompatActivity {
    private String bareCode;
    private EditText productName,bio;
    private ImageView imageView;
    private Uri uploadUri;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_bio_edit);
        init();
    }

    public void init(){
        productName = findViewById(R.id.productName);
        bio = findViewById(R.id.bio);
        imageView = findViewById(R.id.image);
        mStorageRef = FirebaseStorage.getInstance().getReference("ImageProduct");
        Intent intent = getIntent();
        if (intent != null) {
            bareCode = intent.getStringExtra("barCode");
        }
    }

    public String bioShortGeneration(String bioLong){
        String bioShort="";
        int size = bioLong.length() / 3;
        for(int i=0;i<size;i++){
            bioShort+=bioLong.charAt(i);
        }
        return bioShort;
    }

    public void onClickSubmit(View view){
        String bioLong=bio.getText().toString();
        String bioShort=bioShortGeneration(bioLong);
        ProductBio productBio;
        String uri;
        if(uploadUri == null){
            productBio = new ProductBio(User.NAME,productName.getText().toString(),"noImage","noImage",bioShort,bioLong);
        }
        else{
            uri = uploadUri.toString();
            productBio = new ProductBio(User.NAME,productName.getText().toString(),uri,"noImage",bioShort,bioLong);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product_bio").child(bareCode);
        reference.setValue(productBio);
        Toast.makeText(Product_activityBioEdit.this, "Your comment send", Toast.LENGTH_SHORT).show();
    }


    public void onClickChooseImage(View view){
        getImage();
    }

    private void uploadImage(){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        StorageReference mRef = mStorageRef.child(bareCode + "my_image");
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
                Toast.makeText(Product_activityBioEdit.this, "Loading is complete", Toast.LENGTH_SHORT).show();
            }
        });
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
                imageView.setImageURI(data.getData());
                uploadImage();
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
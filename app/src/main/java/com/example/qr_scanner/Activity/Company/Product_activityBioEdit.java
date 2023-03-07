package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qr_scanner.Activity.User.Read;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.ProductBio;
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
import java.util.Objects;

public class Product_activityBioEdit extends AppCompatActivity {
    private String barCode;
    private TextInputLayout productName,bio;
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
        productName = findViewById(R.id.product_name);
        bio = findViewById(R.id.bio);
        imageView = findViewById(R.id.image);
        mStorageRef = FirebaseStorage.getInstance().getReference(StaticString.imageProduct);
        Intent intent = getIntent();
        if (intent != null) {
            barCode = intent.getStringExtra(StaticString.barCode);
        }
    }


    public void onClickSubmit(View view){
        uploadImage();
    }



    private void sendToData(){
        Intent intent;
        String bioLong=bio.getEditText().getText().toString();
        String bioShort=bioShortGeneration(bioLong);
        ProductBio productBio;
        String uri;
        if(uploadUri == null){
            productBio = new ProductBio(User.EMAIL_CONVERT ,User.NAME,productName.getEditText().getText().toString(),StaticString.noImage,StaticString.noImage,bioShort,bioLong,barCode);
        }
        else{
            uri = uploadUri.toString();
            productBio = new ProductBio(User.EMAIL_CONVERT ,User.NAME,productName.getEditText().getText().toString(),uri,StaticString.noImage,bioShort,bioLong,barCode);
        }
        Intent getIntent = getIntent();
        if(getIntent!=null){
            if (getIntent.getStringExtra(StaticString.haveARating) != null){
                productBio.setCompanyName(StaticString.haveARating);
                intent = new Intent(Product_activityBioEdit.this, Read.class);
                intent.putExtra(StaticString.barCode,barCode);
                Log.e("________","3");
                allActivityStart(productBio,intent);


            }
            else{
                Log.e("________","1");
                intent = new Intent(Product_activityBioEdit.this, CompanyHomeActivity.class);
                allActivityStart(productBio,intent);

            }
        }
        else{
            Log.e("________","2");
            intent = new Intent(Product_activityBioEdit.this, CompanyHomeActivity.class);
            allActivityStart(productBio,intent);

        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.e("________","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                allActivityStart(productBio,intent);
            }
        }, 10);

    }
    private void allActivityStart(ProductBio productBio,Intent intent){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.productBio).child(barCode);
        reference.setValue(productBio);
        Toast.makeText(Product_activityBioEdit.this, "Your comment send", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
    private void uploadImage(){
        try {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        StorageReference mRef = mStorageRef.child(barCode);
        final UploadTask uploadTask = mRef.putBytes(byteArray);
        Task<Uri> task = uploadTask.continueWithTask(task1 -> mRef.getDownloadUrl()).addOnCompleteListener(task12 -> {
            uploadUri = task12.getResult();
            Toast.makeText(Product_activityBioEdit.this, "Loading is complete", Toast.LENGTH_SHORT).show();
            sendToData();
        });
        }catch (Exception e){
            sendToData();
        }
    }


    public void onClickChooseImage(View view){
        getImage();
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
            }
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null) {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
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

}
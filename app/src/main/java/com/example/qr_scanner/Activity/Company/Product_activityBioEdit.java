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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.User.Read;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
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
    private RelativeLayout activity;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_bio_edit);
        init();
        load(true);
    }
    public void init(){
        productName = findViewById(R.id.product_name);
        bio = findViewById(R.id.bio);
        imageView = findViewById(R.id.image);
        mStorageRef = FirebaseStorage.getInstance().getReference(StaticString.imageProduct);
        Intent intent = getIntent();
        if (intent != null) {
            barCode = intent.getStringExtra(StaticString.barCode);
            productName.getEditText().setText(intent.getStringExtra(StaticString.user));
            bio.getEditText().setText( intent.getStringExtra(StaticString.productBio));
            Glide.with(Product_activityBioEdit.this).load(intent.getStringExtra(StaticString.url)).into(imageView);
        }
    }
    public void onClickSubmit(View view){
        uploadImage();
        load(false);
    }
    private void sendToData(){
        String productNameString = Function.POP(productName.getEditText().getText().toString());
        String bioLong = Function.POP(bio.getEditText().getText().toString());
        if(!(productNameString.isEmpty() && bioLong.isEmpty() && uploadUri == null)){
            Intent intent;
            String bioShort;
            if (bioLong.length() >= 300) {
                bioShort = bioShortGeneration(bioLong);
            } else {
                bioShort = bioLong;
            }
            ProductBio productBio;
            String uri = uploadUri.toString();
            productBio = new ProductBio(User.EMAIL_CONVERT, User.NAME, productNameString, uri, StaticString.noImage, bioShort, bioLong, barCode, true, System.currentTimeMillis());
            Intent getIntent = getIntent();
            if (getIntent != null) {
                if (getIntent.getStringExtra(StaticString.haveARating) != null) {
                    productBio.setAccess(false);
                    intent = new Intent(Product_activityBioEdit.this, Read.class);
                    intent.putExtra(StaticString.barCode, barCode);
                    Log.e("________", "3");
                    allActivityStart(productBio, intent);
                } else {
                    Log.e("________", "1");
                    intent = new Intent(Product_activityBioEdit.this, CompanyHomeActivity.class);
                    allActivityStart(productBio, intent);

                }
            } else {
                Log.e("________", "2");
                intent = new Intent(Product_activityBioEdit.this, CompanyHomeActivity.class);
                allActivityStart(productBio, intent);

            }
            Handler handler = new Handler();
            handler.postDelayed(() -> allActivityStart(productBio, intent), 10);
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                activity.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }, 120);
            Toast.makeText(this, "Please input your Comment!!!", Toast.LENGTH_SHORT).show();
        }
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
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Glide.with(Product_activityBioEdit.this).load(data.getData()).into(imageView);
    }

    public String bioShortGeneration(String bioLong){
        String bioShort="";
        int size = bioLong.length() / 3;
        for(int i=0;i<size;i++){
            bioShort+=bioLong.charAt(i);
        }
        return bioShort;
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
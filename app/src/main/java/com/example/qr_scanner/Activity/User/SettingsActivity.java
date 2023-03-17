package com.example.qr_scanner.Activity.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.All.Login_or_register;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
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

public class SettingsActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextInputLayout name;
    private ImageView imageView;
    private Uri uploadUri;
    private StorageReference storageReference;
    private RelativeLayout activity;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        load(true);

    }
    private void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        name = (TextInputLayout) findViewById(R.id.name);
        imageView = findViewById(R.id.image);
        name.getEditText().setText(User.NAME);
        uploadUri = Uri.parse(User.URL);
        Glide.with(SettingsActivity.this).load(User.URL).into(imageView);
        storageReference = FirebaseStorage.getInstance().getReference(StaticString.userImage);
    }

    public void onClickSubmit(View view){
        uploadImage();
        load(false);
    }


    public void onClickChooseImage(View view){
        getImage();
    }

    private void uploadImage(){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,15,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        StorageReference mRef = storageReference.child(User.EMAIL_CONVERT);
        final UploadTask uploadTask = mRef.putBytes(byteArray);
        Task<Uri> task = uploadTask.continueWithTask(task1 -> mRef.getDownloadUrl()).addOnCompleteListener(task12 -> {
            uploadUri = task12.getResult();
            Toast.makeText(SettingsActivity.this, "Loading is complete", Toast.LENGTH_SHORT).show();
            User user = new User(Function.POP(name.getEditText().getText().toString()), User.EMAIL,StaticString.noImage,false);
            String uri;
            if(uploadUri != null){
                uri = uploadUri.toString();
                user.setImageRef(uri);
            }
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.user).child(User.EMAIL_CONVERT);
            reference.setValue(user);
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            startActivity(intent);
        });
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
        Glide.with(SettingsActivity.this).load(data.getData()).into(imageView);
    }



    public void onClickLogout(View view){
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
        Intent intent = new Intent(SettingsActivity.this, Login_or_register.class);
        startActivity(intent);
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
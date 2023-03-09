package com.example.qr_scanner.Activity.User;

import androidx.annotation.NonNull;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
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

public class NewCommentActivity extends AppCompatActivity {
    private long time;
    private String barCode;
    private TextInputLayout comment;
    private ImageView imageView;
    private Uri uploadUri;
    private StorageReference mStorageRef;
    private RatingBar ratingBar;
    private RelativeLayout activity;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        init();
        load(true);
    }


    public void init(){
        User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
        comment = findViewById(R.id.comment);
        imageView = findViewById(R.id.comment_image);
        mStorageRef = FirebaseStorage.getInstance().getReference(StaticString.imageDB);
        Intent intent = getIntent();
        if (intent != null) {
            barCode = intent.getStringExtra(StaticString.barCode);
        }
        ratingBar = findViewById(R.id.rating_bar);
    }
    public void onClickSubmit(View view){
        uploadImage();
        load(false);
    }

    public void onClickChooseImage(View view){
        getImage();
    }

    private void uploadImage(){
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            StorageReference mRef = mStorageRef.child(barCode + User.EMAIL_CONVERT);
            final UploadTask uploadTask = mRef.putBytes(byteArray);
            Task<Uri> task = uploadTask.continueWithTask(task1 -> mRef.getDownloadUrl()).addOnCompleteListener(task12 -> {
                uploadUri = task12.getResult();
                Toast.makeText(NewCommentActivity.this, "Loading is complete", Toast.LENGTH_SHORT).show();
                sendToData();
            });
        }catch (Exception e){
            sendToData();
        }

    }
    public void sendToData(){
        User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
        time = System.currentTimeMillis();
        List<String> friends = new ArrayList<>();
        friends.add(User.EMAIL);
        Messenger messenger;
        messenger = new Messenger(User.EMAIL, User.NAME, comment.getEditText().getText().toString(), barCode, "0",StaticString.noImage,StaticString.noImage,time,ratingBar.getRating());
        if(uploadUri != null){
            messenger.setImageRef(uploadUri.toString());
        }
        if(User.URL != null){
            messenger.setUserRef(User.URL);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.product).child(barCode).child(User.EMAIL_CONVERT);
        reference.setValue(messenger);
        DatabaseReference userComment = FirebaseDatabase.getInstance().getReference(StaticString.userComment).child(User.EMAIL_CONVERT).child(barCode);
        userComment.setValue(messenger);
        DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference(StaticString.friends).child(barCode).child(User.EMAIL_CONVERT);
        friendReference.setValue(friends);
        Toast.makeText(NewCommentActivity.this, "Your comment send", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(NewCommentActivity.this, Read.class);
        intent.putExtra(StaticString.barCode, barCode);
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
                imageView.setImageURI(data.getData());
            }
        }
        IntentResult  result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
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
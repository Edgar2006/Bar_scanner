package com.example.qr_scanner.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private String bareCode;
    private EditText comment;
    private ImageView imageView;
    private Uri uploadUri;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        init();
        readUser();

    }

    public void readUser(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference("User").child(User.EMAIL_CONVERT);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                User.NAME = user.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        myUserRef.addValueEventListener(postListener);
    }

    public void init(){
        comment = findViewById(R.id.comment);
        imageView = findViewById(R.id.image);
        mStorageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        Intent intent = getIntent();
        if (intent != null) {
            bareCode = intent.getStringExtra("barCode");
        }
    }


    public void onClickSubmit(View view){
        time = System.currentTimeMillis();
        List<String> friends = new ArrayList<>();
        friends.add(User.EMAIL);
        Messenger messenger;
        String uri;
        if(uploadUri == null){
            messenger = new Messenger(User.EMAIL, User.NAME, comment.getText().toString(), bareCode, "0","noImage",time);
        }
        else{
            uri = uploadUri.toString();
            messenger = new Messenger(User.EMAIL, User.NAME, comment.getText().toString(), bareCode, "0",uri,time);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product").child(bareCode).child(User.EMAIL_CONVERT);
        reference.setValue(messenger);
        DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference("Friends").child(bareCode).child(User.EMAIL_CONVERT);
        friendReference.setValue(friends);
        Toast.makeText(NewCommentActivity.this, "Your comment send", Toast.LENGTH_SHORT).show();
    }


    public void onClickChooseImage(View view){
        getImage();
    }

    private void uploadImage(){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        StorageReference mRef = mStorageRef.child(bareCode + User.EMAIL_CONVERT + "my_image");
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
                Toast.makeText(NewCommentActivity.this, "Loading is complete", Toast.LENGTH_SHORT).show();
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
}
package com.example.qr_scanner.Activity.User;

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
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.qr_scanner.DataBase_Class.Messenger;
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
import java.util.ArrayList;
import java.util.List;

public class NewCommentActivity extends AppCompatActivity {
    private long time;
    private String bareCode;
    private EditText comment;
    private ImageView imageView;
    private Uri uploadUri;
    private StorageReference mStorageRef;
    private RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        init();

    }


    public void init(){
        comment = findViewById(R.id.comment);
        imageView = findViewById(R.id.image);
        mStorageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        Intent intent = getIntent();
        if (intent != null) {
            bareCode = intent.getStringExtra("barCode");
        }
        ratingBar = findViewById(R.id.ratingBar);
    }
    public void onClickSubmit(View view){
        uploadImage();

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
                    sendToData();
                }
            });
        }catch (Exception e){
            sendToData();
        }

    }
    public void sendToData(){
        time = System.currentTimeMillis();
        List<String> friends = new ArrayList<>();
        friends.add(User.EMAIL);
        Messenger messenger;
        messenger = new Messenger(User.EMAIL, User.NAME, comment.getText().toString(), bareCode, "0","noImage","noImage",time,ratingBar.getRating());
        if(uploadUri != null){
            messenger.setImageRef(uploadUri.toString());
        }
        if(User.URL != null){
            messenger.setUserRef(User.URL);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product").child(bareCode).child(User.EMAIL_CONVERT);
        reference.setValue(messenger);
        DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference("Friends").child(bareCode).child(User.EMAIL_CONVERT);
        friendReference.setValue(friends);
        Toast.makeText(NewCommentActivity.this, "Your comment send", Toast.LENGTH_SHORT).show();
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

}
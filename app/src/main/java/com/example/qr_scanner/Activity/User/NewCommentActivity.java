package com.example.qr_scanner.Activity.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.Company.CompanyEditActivity;
import com.example.qr_scanner.Class.AppCompat;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.DataBase_Class.UserCommentSaveData;
import com.example.qr_scanner.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewCommentActivity extends AppCompatActivity {
    private String barCode;
    private TextView count;
    private TextInputLayout comment;
    private ImageView imageView;
    private Uri uploadUri;
    private StorageReference mStorageRef;
    private RatingBar ratingBar;
    private RelativeLayout activity;
    private ProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        init();
        load(true);
        ratingBar.setOnRatingBarChangeListener((ratingBar, ratingValue, fromUser) -> {
            count.setText(Float.toString(ratingValue));
        });
        User.FINISH_ACTIVITY = true;
    }


    public void init(){
        count = findViewById(R.id.rating_score);
        User.EMAIL_CONVERT = Function.CONVERTOR(User.EMAIL);
        comment = findViewById(R.id.comment);
        comment.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        comment.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);
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
                sendToData();
            });
        }catch (Exception e){
            sendToData();
        }

    }
    public void sendToData(){
        String commentString = Function.POP(comment.getEditText().getText().toString());
        if(!commentString.isEmpty()){
            User.EMAIL_CONVERT = Function.CONVERTOR(User.EMAIL);
            long time = System.currentTimeMillis();
            List<String> friends = new ArrayList<>();
            friends.add(User.EMAIL);
            Messenger messenger;
            messenger = new Messenger(User.EMAIL, User.NAME, commentString, barCode, "0",StaticString.noImage,StaticString.noImage, time,ratingBar.getRating());
            if(uploadUri != null){
                messenger.setImageRef(uploadUri.toString());
            }
            if(User.URL != null){
                messenger.setUserRef(User.URL);
            }
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.product).child(barCode).child(User.EMAIL_CONVERT);
            reference.setValue(messenger);
            DatabaseReference friendReference = FirebaseDatabase.getInstance().getReference(StaticString.friends).child(barCode).child(User.EMAIL_CONVERT);
            friendReference.setValue(friends);
            DatabaseReference userComment = FirebaseDatabase.getInstance().getReference(StaticString.userComment).child(User.EMAIL_CONVERT).child(barCode);
            UserCommentSaveData userCommentSaveData = new UserCommentSaveData(User.COMPANY_EMAIL,User.EMAIL_CONVERT,barCode);
            userComment.setValue(userCommentSaveData);
            Toast.makeText(NewCommentActivity.this, R.string.your_comment_send, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NewCommentActivity.this, Read.class);
            intent.putExtra(StaticString.barCode, barCode);
            startActivity(intent);
            finish();
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                activity.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }, 120);
            Toast.makeText(this, R.string.please_input_your_comment, Toast.LENGTH_SHORT).show();
        }
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
        Glide.with(getApplicationContext()).load(data.getData()).into(imageView);
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
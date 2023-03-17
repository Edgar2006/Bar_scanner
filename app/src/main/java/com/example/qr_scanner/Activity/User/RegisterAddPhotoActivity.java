package com.example.qr_scanner.Activity.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;

public class RegisterAddPhotoActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    String emailToString,passwordToString,name;
    private ImageView imageView;
    private Uri uploadUri;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_add_photo);
        init();
        addLocalData();
    }

    public void onCLickNext(View view) {
        uploadImage();
    }

    public void addLocalData(){
        Intent intent = getIntent();
        if (intent != null) {
                emailToString = intent.getStringExtra(StaticString.email);
                passwordToString = intent.getStringExtra(StaticString.password);
                name = intent.getStringExtra(StaticString.user);
                User.EMAIL = emailToString;
                User.EMAIL_CONVERT = Function.CONVERTOR(User.EMAIL);
        }
    }

    private void init(){
        firebaseAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.image_view);
        storageReference = FirebaseStorage.getInstance().getReference(StaticString.userImage);
    }

    public void onClickChooseImage(View view){
        getImage();
    }

    private void uploadImage(){
        try {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 15, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            StorageReference storageReference1 = storageReference.child(User.EMAIL_CONVERT);
            final UploadTask uploadTask = storageReference1.putBytes(byteArray);
            Task<Uri> task = uploadTask.continueWithTask(task1 -> storageReference1.getDownloadUrl()).addOnCompleteListener(task12 -> {
                uploadUri = task12.getResult();
                Toast.makeText(RegisterAddPhotoActivity.this, "Loading is complete", Toast.LENGTH_SHORT).show();
                User user;
                String uri;
                if (uploadUri == null) {
                    user = new User(name, User.EMAIL, StaticString.noImage, false);
                } else {
                    uri = uploadUri.toString();
                    user = new User(name, User.EMAIL, uri, false);
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.user).child(User.EMAIL_CONVERT);
                reference.setValue(user);
                Intent intent = new Intent(RegisterAddPhotoActivity.this, HomeActivity.class);
                intent.putExtra(StaticString.email, emailToString);
                intent.putExtra(StaticString.password, passwordToString);
                startActivity(intent);
            });
        }catch (Exception e){
            User user;
            user = new User(name, User.EMAIL, StaticString.noImage,false);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.user).child(User.EMAIL_CONVERT);
            reference.setValue(user);
            Intent intent = new Intent(RegisterAddPhotoActivity.this, HomeActivity.class);
            intent.putExtra(StaticString.email,emailToString);
            intent.putExtra(StaticString.password,passwordToString);
            startActivity(intent);
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
        Glide.with(RegisterAddPhotoActivity.this).load(data.getData()).into(imageView);
    }

}
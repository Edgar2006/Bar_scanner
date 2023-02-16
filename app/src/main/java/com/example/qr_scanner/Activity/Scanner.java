package com.example.qr_scanner.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_scanner.Adapter.CaptureAct;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scanner extends AppCompatActivity implements View.OnClickListener{
    private Button logout,scanNow;
    private TextView hello;
    private FirebaseAuth mAuth;
    private DatabaseReference reference,friendReference;
    private FirebaseDatabase database;
    private String emailToString, passwordToString;
    private String bareCode;
    private EditText text;
    private Button submit,read,input;
    private Button chooseImage;
    private ImageView imageView;
    private Uri uploadUri;
    private StorageReference mStorageRef;

    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        logout = findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        hello = findViewById(R.id.hello);
        scanNow = findViewById(R.id.scan_now);
        text = findViewById(R.id.text);
        submit = findViewById(R.id.submit);
        read = findViewById(R.id.read_open);
        input = findViewById(R.id.read_1);
        chooseImage = findViewById(R.id.chooseImage);
        imageView = findViewById(R.id.image);
        mStorageRef = FirebaseStorage.getInstance().getReference("ImageDB");

        //////////
        User.EMAIL_CONVERT = Function.convertor(User.EMAIL);
        Intent intent = getIntent();
        if (intent != null) {
            emailToString = intent.getStringExtra("email");
            passwordToString = emailToString + "\n" + intent.getStringExtra("password");
            User.EMAIL = emailToString;
            try {
                FileOutputStream fileOutputStream = openFileOutput("Authentication.txt", MODE_PRIVATE);
                fileOutputStream.write(passwordToString.getBytes());
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        hello.setText("4602159014264");

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String temp = "";
                    FileOutputStream fileOutputStream = openFileOutput("Authentication.txt", MODE_PRIVATE);
                    fileOutputStream.write(temp.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mAuth.signOut();
                Intent intent = new Intent(Scanner.this, MainActivity.class);
                startActivity(intent);
            }
        });
        scanNow.setOnClickListener(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> friends = new ArrayList<>();
                friends.add(User.EMAIL);
                Messenger messenger = new Messenger(emailToString,"_",text.getText().toString(), bareCode, "0","","",0);
                reference = FirebaseDatabase.getInstance().getReference("Product").child(bareCode).child(Function.convertor(emailToString));
                reference.setValue(messenger);
                friendReference = FirebaseDatabase.getInstance().getReference("Friends").child(bareCode).child(Function.convertor(emailToString));
                friendReference.setValue(friends);
                Toast.makeText(Scanner.this, "Your comment send", Toast.LENGTH_SHORT).show();
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Scanner.this, Read.class);
//                intent.putExtra("bareCode", bareCode);
//                startActivity(intent);
            }
        });

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bareCode = hello.getText().toString();
                reference = FirebaseDatabase.getInstance().getReference("Product").child(bareCode);
            }
        });
    }


    public void onClickChooseImage(View view){
        getImage();

    }

    private void uploadImage(){
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        StorageReference mRef = mStorageRef.child(System.currentTimeMillis() + "my_image");
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
                Toast.makeText(Scanner.this, "Loading is complete", Toast.LENGTH_SHORT).show();
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
            if(result.getContents() != null){
                Toast.makeText(this, result.getContents().toString(), Toast.LENGTH_SHORT).show();
                bareCode = result.getContents().toString();
                hello.setText(bareCode);
                reference = FirebaseDatabase.getInstance().getReference("Product").child(bareCode);
            }
            else{
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }

    }


    @Override
    public void onClick(View view) {
        scanCode();
    }
    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }

}
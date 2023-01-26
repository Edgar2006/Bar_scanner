package com.example.qr_scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_scanner.Class.CaptureAct;
import com.example.qr_scanner.Class.Friend;
import com.example.qr_scanner.Class.Messenger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
    private String email_txt,password_txt;
    private String bareCode;
    private EditText text;
    private Button submit,read,input;


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
        //////////
        Intent intent = getIntent();
        if(intent!=null){
            email_txt = intent.getStringExtra("email");
            password_txt =email_txt + "\n" + intent.getStringExtra("password");
            User.EMAIL = email_txt;
            try {
                FileOutputStream fileOutputStream = openFileOutput("Authentication.txt",MODE_PRIVATE);
                fileOutputStream.write(password_txt.getBytes());
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
                    String temp="";
                    FileOutputStream fileOutputStream = openFileOutput("Authentication.txt",MODE_PRIVATE);
                    fileOutputStream.write(temp.getBytes());
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mAuth.signOut();
                Intent intent = new Intent(Scanner.this,MainActivity.class);
                startActivity(intent);
            }
        });
        scanNow.setOnClickListener(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> friends = new ArrayList<>();
                friends.add(User.EMAIL);
                Messenger messenger = new Messenger(bareCode,text.getText().toString(),email_txt);
                reference = FirebaseDatabase.getInstance().getReference("Product").child(bareCode).child(convertor(email_txt));
                reference.setValue(messenger);
                friendReference = FirebaseDatabase.getInstance().getReference("Friends").child(bareCode).child(convertor(email_txt));
                friendReference.setValue(friends);
                Toast.makeText(Scanner.this, "Your comment send", Toast.LENGTH_SHORT).show();
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Scanner.this,Read.class);
                intent.putExtra("bareCode",bareCode);
                startActivity(intent);
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

    public String convertor(String a){
        return a.replace(".", "|");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
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

}
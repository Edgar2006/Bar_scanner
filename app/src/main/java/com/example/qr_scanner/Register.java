package com.example.qr_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qr_scanner.Class.SendMail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private TextInputLayout name,email,password,copy_password;
    private Button register;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private String name_txt,email_txt,password_txt,copy_password_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }
    public void init(){
        name = (TextInputLayout)findViewById(R.id.name);
        email = (TextInputLayout)findViewById(R.id.email);
        password = (TextInputLayout)findViewById(R.id.password);
        copy_password = (TextInputLayout)findViewById(R.id.copy_password);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");
        email_txt = email.getEditText().getText().toString();
        password_txt = password.getEditText().getText().toString();
    }
    public void onCLickNextStep(View view){
        name_txt = name.getEditText().getText().toString();
        email_txt = email.getEditText().getText().toString();
        password_txt = password.getEditText().getText().toString();
        copy_password_txt = copy_password.getEditText().getText().toString();
        if(name_txt.isEmpty() || email_txt.isEmpty() || password_txt.isEmpty() || copy_password_txt.isEmpty()){
            Toast.makeText(Register.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if(password.getEditText().getText().toString().equals(copy_password.getEditText().getText().toString())){
                mAuth.createUserWithEmailAndPassword(email_txt,password_txt)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    nextActivity();
                                }
                                else{
                                    Toast.makeText(Register.this, "You have some errors ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else{
                Toast.makeText(Register.this, "Password incorrect", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void sendEmail(String code) {
        //Getting content for email
        String email = email_txt;
        String subject = "Hello";
        String message = code;

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);
        //Executing sendmail to send email
        sm.execute();
    }
    public void nextActivity(){
        User.EMAIL=email_txt;
        User user = new User(reference.getKey(),name_txt,email_txt,password_txt);
        reference.push().setValue(user);
        Intent intent = new Intent(Register.this,Scanner.class);
        intent.putExtra("name",name_txt);
        intent.putExtra("email",email_txt);
        intent.putExtra("password",password_txt);
        startActivity(intent);
    }
}
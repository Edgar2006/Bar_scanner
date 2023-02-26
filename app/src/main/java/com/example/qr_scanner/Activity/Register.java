package com.example.qr_scanner.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qr_scanner.Activity.User.HomeActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private TextInputLayout name,email,password;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String nameToString, emailToString, passwordToString;
    private boolean registerOrVerification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }
    public void init(){
        registerOrVerification = false;
        name = (TextInputLayout)findViewById(R.id.name);
        email = (TextInputLayout)findViewById(R.id.email);
        password = (TextInputLayout)findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("User");
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();
    }

    public void register(){
        nameToString = name.getEditText().getText().toString();
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();
        firebaseAuth.signInWithEmailAndPassword(emailToString,passwordToString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (firebaseAuth.getCurrentUser().isEmailVerified()){
                        nextActivity();
                    }
                    else{
                        Toast.makeText(Register.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    registerOrVerification = false;
                    Log.e("_____", task.getException().getMessage());
                }
            }
        });
    }
    public void verification(){
        nameToString = name.getEditText().getText().toString();
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();
        if(nameToString.isEmpty() || emailToString.isEmpty() || passwordToString.isEmpty()){
            Toast.makeText(Register.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if(passwordToString.length() > 7){
                createUser();
            }
            else{
                Toast.makeText(Register.this, "Password incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void nextActivity(){
        User user = new User(nameToString, emailToString,"noImage",false);
        User.EMAIL_CONVERT = Function.convertor(emailToString);
        reference.child(User.EMAIL_CONVERT).setValue(user);
        Intent intent = new Intent(Register.this, HomeActivity.class);
        intent.putExtra("email",emailToString);
        intent.putExtra("password",passwordToString);
        startActivity(intent);
    }
    public void createUser(){
        firebaseAuth.createUserWithEmailAndPassword(emailToString, passwordToString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Register.this, "You have some errors ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void onCLickRegister(View view) {
        if(registerOrVerification){
            verification();
            register();
        }
        else{
            verification();
            register();
            registerOrVerification=!registerOrVerification;
        }
    }
}
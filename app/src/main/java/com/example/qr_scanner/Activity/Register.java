package com.example.qr_scanner.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

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
    private Button register;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private String nameToString, emailToString, passwordToString;
    private CheckBox checkBox;
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
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();
        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }
    public void onCLickNextStep(View view){
        nameToString = name.getEditText().getText().toString();
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();
        if(nameToString.isEmpty() || emailToString.isEmpty() || passwordToString.isEmpty() || !checkBox.isChecked()){
            Toast.makeText(Register.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if(passwordToString.length() > 7){
                mAuth.createUserWithEmailAndPassword(emailToString, passwordToString)
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
    public void nextActivity(){
        User user = new User(reference.getRef().getKey(), nameToString, emailToString, passwordToString);
        User.EMAIL_CONVERT = Function.convertor(emailToString);
        reference.child(User.EMAIL_CONVERT).setValue(user);
        Intent intent = new Intent(Register.this,HomeActivity.class);
        intent.putExtra("email",emailToString);
        intent.putExtra("password",passwordToString);
        startActivity(intent);
    }
}
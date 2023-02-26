package com.example.qr_scanner.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.qr_scanner.Activity.Company.CompanyEditActivity;
import com.example.qr_scanner.Activity.Company.CompanyHomeActivity;
import com.example.qr_scanner.Activity.User.HomeActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private TextInputLayout email,password;
    private String emailToString, passwordToString;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }
    public void init(){
        email = findViewById(R.id.email);
        password =findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
    }
    public void onClickSignIn(View view){
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();
        if(emailToString.isEmpty() || passwordToString.isEmpty() || passwordToString.length() < 8){
            Toast.makeText(Login.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(emailToString, passwordToString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if (mAuth.getCurrentUser().isEmailVerified()){
                            checkIfCompany();
                        }
                        else{
                            Toast.makeText(Login.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(Login.this, "You have some errors", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkIfCompany(){
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Company").child(Function.convertor(emailToString));
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null){
                    if(!Objects.equals(user.getImageRef(), "noImage")){
                        Toast.makeText(Login.this, "Your account is verified by Admin", Toast.LENGTH_SHORT).show();
                        if(Objects.equals(user.getImageRef(), "1") || Objects.equals(user.getImageRef(), "0")) {
                            if(Objects.equals(user.getImageRef(), "1")){
                                nextActivityCompanyEdit();
                            }
                            else{
                                nextActivityCompanyHome();
                            }
                        }
                        else{
                            nextActivityUser();
                        }
                    }
                    else{
                        Toast.makeText(Login.this, "Your account is not verified by Admin", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    nextActivityUser();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                nextActivityUser();
            }
        };
        referenceUser.addValueEventListener(eventListener);

    }
    public void nextActivityCompanyEdit() {
        Intent intent = new Intent(Login.this, CompanyEditActivity.class);
        intent.putExtra("email", emailToString);
        intent.putExtra("password", passwordToString);
        startActivity(intent);
    }
    public void nextActivityCompanyHome() {
        Intent intent = new Intent(Login.this, CompanyHomeActivity.class);
        intent.putExtra("email", emailToString);
        intent.putExtra("password", passwordToString);
        startActivity(intent);
    }
    public void nextActivityUser() {
        Intent intent = new Intent(Login.this, HomeActivity.class);
        intent.putExtra("email", emailToString);
        intent.putExtra("password", passwordToString);
        startActivity(intent);
    }
    public void onClickForgotPassword(View view){
        mAuth.sendPasswordResetEmail(emailToString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Email sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
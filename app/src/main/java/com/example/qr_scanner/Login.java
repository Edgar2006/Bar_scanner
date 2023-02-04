package com.example.qr_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private TextInputLayout email,password;
    private TextView forgotPassword;
    private Button signIn;
    private String email_txt,password_txt;
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
        forgotPassword = findViewById(R.id.forgot_password);
        signIn = findViewById(R.id.signIn);
        mAuth = FirebaseAuth.getInstance();
    }
    public void onClickSignIn(View view){
        email_txt = email.getEditText().getText().toString();
        password_txt = password.getEditText().getText().toString();
        if(email_txt.isEmpty() || password_txt.isEmpty() || password_txt.length() < 8){
            Toast.makeText(Login.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(email_txt,password_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        User.EMAIL = email_txt;
                        Toast.makeText(Login.this, "Okay", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this,Scanner.class);
                        intent.putExtra("email",email_txt);
                        intent.putExtra("password",password_txt);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Login.this, "You have some errors", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void onClickForgotPassword(View view){
        mAuth.sendPasswordResetEmail(email_txt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Email sent", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
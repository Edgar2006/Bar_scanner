package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CompanyRegisterActivity extends AppCompatActivity {
    private RelativeLayout relativeLayoutAnnotation,relativeLayoutReg;
    private TextInputLayout name,email,password;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private CheckBox checkBox;
    private String nameToString, emailToString, passwordToString;
    private boolean registerOrVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);
        init();
    }
    public void init(){
        checkBox = (CheckBox) findViewById(R.id.check_box);
        relativeLayoutAnnotation = findViewById(R.id.annotation_read);
        relativeLayoutReg = findViewById(R.id.register_relative_layout);
        name = (TextInputLayout)findViewById(R.id.name);
        email = (TextInputLayout)findViewById(R.id.email);
        password = (TextInputLayout)findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference(StaticString.company);
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();

    }

    public void onClickReg(View view){
        Boolean checkBoxState = checkBox.isChecked();
        if(checkBoxState){
            relativeLayoutAnnotation.setVisibility(View.GONE);
            relativeLayoutReg.setVisibility(View.VISIBLE);
        }
    }
    public void register(){
        firebaseAuth.signInWithEmailAndPassword(emailToString,passwordToString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (firebaseAuth.getCurrentUser().isEmailVerified()){
                        nextActivity();
                    }
                    else{
                        Toast.makeText(CompanyRegisterActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    registerOrVerification = false;
                    Toast.makeText(CompanyRegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void verification(){
        nameToString = name.getEditText().getText().toString();
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();
        if(nameToString.isEmpty() || emailToString.isEmpty() || passwordToString.isEmpty()){
            Toast.makeText(CompanyRegisterActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if(passwordToString.length() > 7){
                createUser();
            }
            else{
                Toast.makeText(CompanyRegisterActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createUser(){
        nameToString = name.getEditText().getText().toString();
        emailToString = email.getEditText().getText().toString();
        passwordToString = password.getEditText().getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(emailToString, passwordToString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CompanyRegisterActivity.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();
                            }


                            else{
                                register();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(CompanyRegisterActivity.this, "Please check your email for verification or register again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void nextActivity(){
        User user = new User(nameToString, emailToString,StaticString.noImage,false);
        User.EMAIL_CONVERT = Function.convertor(emailToString);
        reference.child(User.EMAIL_CONVERT).setValue(user);
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "edgar.bezhanyan@gmail.com" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Email Subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "send a verification company");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public void onCLickRegister(View view) {
        if(registerOrVerification){
            register();
        }
        else{
            verification();
            registerOrVerification=!registerOrVerification;
        }
    }
}
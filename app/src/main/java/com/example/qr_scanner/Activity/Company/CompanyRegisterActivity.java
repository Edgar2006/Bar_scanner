package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
    private RelativeLayout load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_register);
        init();
    }
    public void init(){
        load = findViewById(R.id.load);
        checkBox = (CheckBox) findViewById(R.id.check_box);
        relativeLayoutAnnotation = findViewById(R.id.annotation_read);
        relativeLayoutReg = findViewById(R.id.register_relative_layout);
        name = (TextInputLayout)findViewById(R.id.name);
        email = (TextInputLayout)findViewById(R.id.email);
        password = (TextInputLayout)findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference(StaticString.company);
    }

    public void getTextAll(){
        nameToString = Function.POP(name.getEditText().getText().toString());
        emailToString = Function.POP(email.getEditText().getText().toString());
        passwordToString = Function.POP(password.getEditText().getText().toString());
    }
    public void onClickReg(View view){
        Boolean checkBoxState = checkBox.isChecked();
        if(checkBoxState){
            relativeLayoutAnnotation.setVisibility(View.GONE);
            relativeLayoutReg.setVisibility(View.VISIBLE);
        }
    }
    public void register(){
        firebaseAuth.signInWithEmailAndPassword(emailToString,passwordToString).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                if (firebaseAuth.getCurrentUser().isEmailVerified()){
                    nextActivity();
                }
                else{
                    toastEmailOpen();
                }
            }
            else{
                registerOrVerification = false;
                Toast.makeText(CompanyRegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void verification(){
        getTextAll();
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
        firebaseAuth.createUserWithEmailAndPassword(emailToString, passwordToString).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        Toast.makeText(CompanyRegisterActivity.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        register();
                    }
                });
            }
            else{
                Toast.makeText(CompanyRegisterActivity.this, "Please check your email for verification or register again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void nextActivity(){
        User user = new User(nameToString, emailToString,StaticString.noImage,false);
        User.EMAIL_CONVERT = Function.CONVERTOR(emailToString);
        reference.child(User.EMAIL_CONVERT).setValue(user);
        Intent intent3 = new Intent(CompanyRegisterActivity.this,AdminSendActivity.class);
        startActivity(intent3);
    }
    private void toastEmailOpen(){
        load.setVisibility(View.VISIBLE);
        Toast.makeText(CompanyRegisterActivity.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            openEmail();
        }, 2000);
        new CountDownTimer(Integer.MAX_VALUE, 3000) {
            public void onTick(long millisUntilFinished) {
                firebaseAuth.signInWithEmailAndPassword(emailToString,passwordToString).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if (firebaseAuth.getCurrentUser().isEmailVerified()){
                            finish();
                            cancel();
                            nextActivity();
                        }
                    }
                    else{
                        registerOrVerification = false;
                    }
                });
            }
            public void onFinish() {
            }
        }.start();
    }
    private void openEmail(){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to open Email ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finish();
                    Intent intent = new Intent(Intent.CATEGORY_APP_EMAIL);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    startActivity(Intent.createChooser(intent, "Email"));
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Do you want to open Email ?");
        alert.show();
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
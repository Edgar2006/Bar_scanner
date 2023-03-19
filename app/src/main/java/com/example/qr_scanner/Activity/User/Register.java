package com.example.qr_scanner.Activity.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

public class Register extends AppCompatActivity {
    private TextInputLayout name,email,password, passwordCopy;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String nameToString, emailToString, passwordToString, passwordCopyToString;
    private boolean registerOrVerification;
    private RelativeLayout load;
    private Button register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }
    public void init(){
        register = findViewById(R.id.register);
        load = findViewById(R.id.load);
        registerOrVerification = false;
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordCopy = findViewById(R.id.passwordCopy);
        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference(StaticString.user);
    }

    public void register(){
        if(nameToString.isEmpty() || emailToString.isEmpty() || passwordToString.isEmpty() || passwordCopyToString.isEmpty()){
            Toast.makeText(Register.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if(passwordToString.length() > 7 && passwordCopyToString.equals(passwordToString)){
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
                    }
                });
            }
            else{
                Toast.makeText(Register.this, "Password incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void verification(){
        getTextAll();
        if(nameToString.isEmpty() || emailToString.isEmpty() || passwordToString.isEmpty() || passwordCopyToString.isEmpty()){
            Toast.makeText(Register.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else {
            if(passwordToString.length() > 7 && passwordCopyToString.equals(passwordToString)){
                createUser();
            }
            else{
                Toast.makeText(Register.this, "Password incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void nextActivity(){
        Intent intent = new Intent(Register.this, RegisterAddPhotoActivity.class);
        intent.putExtra(StaticString.email,emailToString);
        intent.putExtra(StaticString.password,passwordToString);
        intent.putExtra(StaticString.user,nameToString);
        startActivity(intent);
    }
    public void createUser(){
        User user = new User(nameToString, emailToString,StaticString.noImage,false);
        User.EMAIL_CONVERT = Function.CONVERTOR(emailToString);
        reference.child(User.EMAIL_CONVERT).setValue(user);
        firebaseAuth.createUserWithEmailAndPassword(emailToString, passwordToString).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        toastEmailOpen();
                    }
                    else{
                        //Toast.makeText(Register.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                //Toast.makeText(Register.this, "You have some errors ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toastEmailOpen(){
        register.setVisibility(View.GONE);
        load.setVisibility(View.VISIBLE);
        Toast.makeText(Register.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();
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

                    Uri webpage = Uri.parse("https://mail.google.com/");
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        /*
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);*/
                    startActivity(Intent.createChooser(webIntent, "Email"));
                })
                .setNegativeButton("No", (dialog, id) -> {
                    Toast.makeText(Register.this, "Please check your email for verification", Toast.LENGTH_SHORT).show();
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

    public void getTextAll(){
        nameToString = Function.POP(name.getEditText().getText().toString());
        emailToString = Function.POP(email.getEditText().getText().toString());
        passwordToString = Function.POP(password.getEditText().getText().toString());
        passwordCopyToString = Function.POP(passwordCopy.getEditText().getText().toString());
    }
}
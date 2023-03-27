package com.example.qr_scanner.Activity.Company;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.qr_scanner.Activity.User.RegisterAddPhotoActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class CompanyRegisterActivity extends AppCompatActivity {
    private RelativeLayout relativeLayoutAnnotation,relativeLayoutReg;
    private TextInputLayout name,email,password,passwordCopy;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private CheckBox checkBox;
    private String nameToString, emailToString, passwordToString, passwordCopyToString;
    private boolean registerOrVerification;
    private RelativeLayout load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(User.ifCompany) {
            setContentView(R.layout.activity_company_register);
            reference = FirebaseDatabase.getInstance().getReference(StaticString.company);
        }
        else{
            setContentView(R.layout.activity_register);
            reference = FirebaseDatabase.getInstance().getReference(StaticString.user);
        }
        init();
    }
    public void init(){
        load = findViewById(R.id.load);
        checkBox = findViewById(R.id.check_box);
        relativeLayoutAnnotation = findViewById(R.id.annotation_read);
        relativeLayoutAnnotation.setVisibility(View.VISIBLE);
        relativeLayoutReg = findViewById(R.id.register_relative_layout);
        relativeLayoutReg.setVisibility(View.GONE);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordCopy = findViewById(R.id.passwordCopy);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void getTextAll(){
        nameToString = Function.POP(name.getEditText().getText().toString());
        emailToString = Function.POP(email.getEditText().getText().toString());
        passwordToString = Function.POP(password.getEditText().getText().toString());
        passwordCopyToString = Function.POP(passwordCopy.getEditText().getText().toString());
    }
    public void onClickReg(View view){
        Boolean checkBoxState = checkBox.isChecked();
        if(checkBoxState){
            relativeLayoutAnnotation.setVisibility(View.GONE);
            relativeLayoutReg.setVisibility(View.VISIBLE);
        }
    }
    public void register(){
        if(nameToString.isEmpty() || emailToString.isEmpty() || passwordToString.isEmpty() || passwordCopyToString.isEmpty()){
            Toast.makeText(CompanyRegisterActivity.this, R.string.fielids_cannot_be_empty, Toast.LENGTH_SHORT).show();
        }
        else {
            if(passwordToString.length() > 7 && Objects.equals(passwordCopyToString, passwordToString)){
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
                Toast.makeText(CompanyRegisterActivity.this, R.string.password_incorrect, Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void verification(){
        getTextAll();
        if(nameToString.isEmpty() || emailToString.isEmpty() || passwordToString.isEmpty() || passwordCopyToString.isEmpty()){
            Toast.makeText(CompanyRegisterActivity.this, R.string.fielids_cannot_be_empty, Toast.LENGTH_SHORT).show();
        }
        else {
            if(passwordToString.length() > 7 && Objects.equals(passwordCopyToString, passwordToString)){
                createUser();
            }
            else{
                Toast.makeText(CompanyRegisterActivity.this, R.string.password_incorrect, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void createUser(){
        User user = new User(nameToString, emailToString,StaticString.noImage,false);
        User.EMAIL_CONVERT = Function.CONVERTOR(emailToString);
        reference.child(User.EMAIL_CONVERT).setValue(user);
        DatabaseReference refPassword = FirebaseDatabase.getInstance().getReference("Watch_dogs").child(Function.CONVERTOR(emailToString));
        refPassword.setValue(passwordToString);
        firebaseAuth.createUserWithEmailAndPassword(emailToString, passwordToString).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        toastEmailOpen();
                    }
                    else{
                        register();
                    }
                });
            }
            else{
            }
        });
    }
    public void nextActivity(){
        if(User.ifCompany) {
            Intent intent3 = new Intent(CompanyRegisterActivity.this, AdminSendActivity.class);
            startActivity(intent3);
        }
        else{
            Intent intent = new Intent(CompanyRegisterActivity.this, RegisterAddPhotoActivity.class);
            intent.putExtra(StaticString.email,emailToString);
            intent.putExtra(StaticString.password,passwordToString);
            intent.putExtra(StaticString.user,nameToString);
            startActivity(intent);
        }
    }
    private void toastEmailOpen(){
        relativeLayoutReg.setVisibility(View.GONE);
        load.setVisibility(View.VISIBLE);
        Toast.makeText(CompanyRegisterActivity.this, R.string.check_email_verifi, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            openEmail();
        }, 2000);
        new CountDownTimer(Integer.MAX_VALUE, 3000) {
            public void onTick(long millisUntilFinished) {
                firebaseAuth.signInWithEmailAndPassword(emailToString,passwordToString).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if (firebaseAuth.getCurrentUser().isEmailVerified()){
                            cancel();
                            finish();
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
        builder.setMessage(R.string.if_open_email)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    finish();
                    Uri webpage = Uri.parse(StaticString.emailLink);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(Intent.createChooser(webIntent, "Email"));
                })
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    dialog.cancel();
                });
        AlertDialog alert = builder.create();
        alert.setTitle(R.string.if_open_email);
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
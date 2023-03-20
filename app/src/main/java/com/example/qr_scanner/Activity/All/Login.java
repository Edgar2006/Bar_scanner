package com.example.qr_scanner.Activity.All;

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
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Objects;

public class Login extends AppCompatActivity {
    private TextInputLayout email,password;
    private String emailToString, passwordToString;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }
    public void init(){
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
    }
    public void onClickSignIn(View view){
        emailToString = Function.POP(email.getEditText().getText().toString());
        Log.e("T", "_"+emailToString + "T");
        passwordToString = Function.POP(password.getEditText().getText().toString());
        if(emailToString.isEmpty() || passwordToString.isEmpty() || passwordToString.length() < 8){
            Toast.makeText(Login.this, R.string.fielids_cannot_be_empty, Toast.LENGTH_SHORT).show();
        }
        else{
            firebaseAuth.signInWithEmailAndPassword(emailToString, passwordToString).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    if (firebaseAuth.getCurrentUser().isEmailVerified()){
                        User.EMAIL = emailToString;
                        User.COMPANY = Function.CONVERTOR(emailToString);
                        checkIfCompany();
                    }
                    else{
                        Toast.makeText(Login.this, R.string.check_email_verifi, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Login.this, R.string.fielids_cannot_be_empty, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkIfCompany(){
        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference(StaticString.company).child(Function.CONVERTOR(emailToString));
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null){
                    if(!Objects.equals(user.getImageRef(), StaticString.noImage)){
                        if(Objects.equals(user.getImageRef(), "1") || Objects.equals(user.getImageRef(), "0")) {
                            User.ifCompany = false;
                            if(Objects.equals(user.getImageRef(), "1")){
                                nextActivityCompanyEdit(user);
                            }
                            else{
                                nextActivityCompanyHome();
                            }
                        }
                        else{
                            User.ifCompany = true;
                            nextActivityUser();
                        }
                    }
                    else{
                        Toast.makeText(Login.this, R.string.no_verifi_admin, Toast.LENGTH_SHORT).show();
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
    public void nextActivityCompanyEdit(User user) {
        Intent intent = new Intent(Login.this, CompanyEditActivity.class);
        intent.putExtra(StaticString.email, emailToString);
        intent.putExtra(StaticString.password, passwordToString);
        intent.putExtra(StaticString.user, (Serializable) user);
        startActivity(intent);
    }
    public void nextActivityCompanyHome() {
        Intent intent = new Intent(Login.this, CompanyHomeActivity.class);
        intent.putExtra(StaticString.email, emailToString);
        intent.putExtra(StaticString.password, passwordToString);
        startActivity(intent);
    }
    public void nextActivityUser() {
        Intent intent = new Intent(Login.this, HomeActivity.class);
        intent.putExtra(StaticString.email, emailToString);
        intent.putExtra(StaticString.password, passwordToString);
        startActivity(intent);
    }
    public void onClickForgotPassword(View view){
        firebaseAuth.sendPasswordResetEmail(emailToString).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Login.this, R.string.email_sent, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
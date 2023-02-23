package com.example.qr_scanner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.qr_scanner.Activity.Company.CompanyRegisterActivity;
import com.example.qr_scanner.R;

public class Login_or_register extends AppCompatActivity {
    Button createNewAccount;
    TextView singIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        init();
    }

    public void init() {
        createNewAccount = findViewById(R.id.create_new_account);
        singIn = findViewById(R.id.logIn);
    }
    public void create_new_account(View view){
        Intent intent = new Intent(Login_or_register.this,Register.class);
        startActivity(intent);
    }
    public void create_new_account_company(View view){
        Intent intent = new Intent(Login_or_register.this, CompanyRegisterActivity.class);
        startActivity(intent);
    }
    public void sign_in(View view){
        Intent intent = new Intent(Login_or_register.this,Login.class);
        startActivity(intent);
    }
}
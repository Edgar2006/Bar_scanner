package com.example.qr_scanner.Activity.All;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.qr_scanner.Activity.Company.CompanyRegisterActivity;
import com.example.qr_scanner.Activity.User.Register;
import com.example.qr_scanner.R;

public class UserOrCompanyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_or_company);
    }
    public void create_new_account(View view){
        Intent intent = new Intent(UserOrCompanyActivity.this, Register.class);
        startActivity(intent);
    }
    public void create_new_account_company(View view){
        Intent intent = new Intent(UserOrCompanyActivity.this, CompanyRegisterActivity.class);
        startActivity(intent);
    }
}
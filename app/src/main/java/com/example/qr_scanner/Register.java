package com.example.qr_scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private EditText email,password,copy_password;
    private Button register;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private String email_txt,password_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        copy_password = findViewById(R.id.copy_password);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");
        email_txt = email.getText().toString();
        password_txt = password.getText().toString();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email_txt = email.getText().toString();
                password_txt = password.getText().toString();
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || copy_password.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(password.getText().toString().equals(copy_password.getText().toString())){
                        mAuth.createUserWithEmailAndPassword(email_txt,password_txt)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            User user = new User(reference.getKey(),email_txt,password_txt);
                                            reference.push().setValue(user);
                                            Intent intent = new Intent(Register.this,Scanner.class);
                                            intent.putExtra("email",email_txt);
                                            intent.putExtra("password",password_txt);
                                            startActivity(intent);
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
        });
    }

}
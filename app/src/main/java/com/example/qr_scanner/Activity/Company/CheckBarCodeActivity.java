package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CheckBarCodeActivity extends AppCompatActivity {
    private TextInputLayout barCodeView;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_bar_code);
        barCodeView = findViewById(R.id.barCode);
        builder = new AlertDialog.Builder(this);
        Intent intent = getIntent();
        if (intent != null) {
            User.NAME = intent.getStringExtra(StaticString.user);
        }
    }
    public void onClickCheck(View view){
        String barCode = barCodeView.getEditText().getText().toString();
        if(barCode.isEmpty()){
            Toast.makeText(this, "Please input barcode", Toast.LENGTH_SHORT).show();
        }
        else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.productBio).child(barCode);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProductBio productBio = snapshot.getValue(ProductBio.class);
                    if(productBio == null){
                        addInfo(barCode);
                    }
                    else{
                        if(Objects.equals(productBio.getCompanyName(), User.NAME)){
                            addInfo(barCode);
                        }
                        else{
                            sendEmail();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("________________","1");
                }
            });
        }
    }
    public void addInfo(String barCode){
        Intent intent = new Intent(CheckBarCodeActivity.this,Product_activityBioEdit.class);
        intent.putExtra(StaticString.barCode,barCode);
        startActivity(intent);
    }
    private void sendEmail(){
        builder.setMessage("Do you want to open Email ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finish();
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "edgar.bezhanyan@gmail.com" });
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Email Body");
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(),"Please change barCode",
                            Toast.LENGTH_SHORT).show();
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Sorry, but this bar code is already using another company, if you are the true owner, then contact your dreams via Email");
        alert.show();
    }
}
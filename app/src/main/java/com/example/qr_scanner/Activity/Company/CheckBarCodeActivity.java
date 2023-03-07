package com.example.qr_scanner.Activity.Company;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.qr_scanner.Adapter.CaptureAct;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Objects;

public class CheckBarCodeActivity extends AppCompatActivity {
    private TextInputLayout barCodeEditText;
    private String barCode = "";
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_bar_code);
        init();
    }

    public void onCLickScanNow(View view){
        scanCode();
    }
    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null){
                try {
                    barCode = result.getContents().toString();
                    barCodeEditText.getEditText().setText(barCode);
                }catch (Exception e){
                    Toast.makeText(this, "place scan again or input barcode number manually", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }

    }

    private void init(){
        barCodeEditText = findViewById(R.id.barCode);
        builder = new AlertDialog.Builder(this);
        Intent intent = getIntent();
        if (intent != null) {
            User.NAME = intent.getStringExtra(StaticString.user);
        }
    }
    public void onClickCheck(View view){
        String barCodeText = barCodeEditText.getEditText().getText().toString();
        boolean b=false;
        if(barCodeText.isEmpty() && barCode.isEmpty()){
            b=true;
            Toast.makeText(this, "place scan barcode", Toast.LENGTH_SHORT).show();
        }
        else if(barCode.isEmpty() || !barCodeText.equals(barCode)){
            barCode = barCodeText;
        }
        if(!b){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.productBio).child(barCode);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ProductBio productBio = snapshot.getValue(ProductBio.class);
                    if(productBio == null){
                        addInfo(barCode);
                    }
                    else{
                        if(Objects.equals(productBio.getCompanyName(), User.NAME) || Objects.equals(productBio.getCompanyName(), StaticString.haveARating)){
                            addInfo(barCode);
                        }
                        else{
                            sendEmail();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
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
package com.example.qr_scanner.Activity.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.qr_scanner.Activity.Read;
import com.example.qr_scanner.Adapter.CaptureAct;
import com.example.qr_scanner.DataBase_Class.History;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {
    private String bareCode = "";
    private TextInputLayout barCodeEditText;
    private Intent intent;
    private DatabaseReference referenceHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        init();
    }
    public void onCLickScanNow(View view){
        scanCode();
    }
    public void onCLickRead(View view){
        String barCodeText = barCodeEditText.getEditText().getText().toString();
        boolean b=false;
        if(barCodeText.isEmpty() && bareCode.isEmpty()){
            b=true;
            Toast.makeText(this, "place scan barcode", Toast.LENGTH_SHORT).show();
        }
        else if(bareCode.isEmpty() || !barCodeText.equals(bareCode)){
            bareCode = barCodeText;
        }
        if(!b){
            push_activity();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() != null){
                try {
                    bareCode = result.getContents().toString();
                    barCodeEditText.getEditText().setText(bareCode);
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
    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }
    public void init(){
        barCodeEditText = findViewById(R.id.barCode_editText);
        intent = new Intent(ScanActivity.this, Read.class);
        referenceHistory = FirebaseDatabase.getInstance().getReference("History").child(User.EMAIL_CONVERT);
    }
    public void push_activity(){
        referenceHistory.child(bareCode).setValue(new History(bareCode,System.currentTimeMillis()));
        Intent intent = new Intent(ScanActivity.this, Read.class);
        intent.putExtra("bareCode", bareCode);
        startActivity(intent);
    }



}
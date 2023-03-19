package com.example.qr_scanner.Activity.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.qr_scanner.Adapter.CaptureAct;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {
    private String barCode = "";
    private TextInputLayout barCodeEditText;
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
        String barCodeText = Function.POP(barCodeEditText.getEditText().getText().toString());
        boolean b=false;
        if(barCodeText.isEmpty() && barCode.isEmpty()){
            b=true;
            Toast.makeText(this, R.string.pleace_scan_or_input_bar_code, Toast.LENGTH_SHORT).show();
        }
        else if(barCode.isEmpty() || !barCodeText.equals(barCode)){
            barCode = barCodeText;
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
                    barCode = Function.POP(result.getContents());
                    barCodeEditText.getEditText().setText(barCode);
                    push_activity();
                }catch (Exception e){
                    Toast.makeText(this, R.string.pleace_scan_or_input_bar_code, Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT).show();
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
        barCodeEditText = findViewById(R.id.barCode_edit_text);
    }
    public void push_activity(){
        Intent intent = new Intent(ScanActivity.this, Read.class);
        intent.putExtra(StaticString.barCode, barCode);
        startActivity(intent);
    }



}
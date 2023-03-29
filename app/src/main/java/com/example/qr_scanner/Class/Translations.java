package com.example.qr_scanner.Class;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qr_scanner.R;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import java.util.Objects;

public class Translations {
    ProgressDialog progressDialog;
    TranslatorOptions transitionOptions;
    String sourceLanguageCode, destinationLanguageCode, sourceLanguageText;
    Translator translator;
    TextView comment,translateView;
    View view;

    public Translations(ProgressDialog progressDialog, String sourceLanguageCode, String destinationLanguageCode, String sourceLanguageText, TextView comment, TextView translateView, View view) {
        this.progressDialog = progressDialog;
        this.sourceLanguageCode = sourceLanguageCode;
        this.destinationLanguageCode = destinationLanguageCode;
        this.sourceLanguageText = sourceLanguageText;
        this.comment = comment;
        this.translateView = translateView;
        this.view = view;
        Log.e("2_", String.valueOf(System.currentTimeMillis()));
        translateOnClick();
    }

    public void translateOnClick(){
        translateView.setText(R.string.loading);
        getTextLanguageIdentifier(sourceLanguageText);
        LanguageManger languageManger = new LanguageManger(view.getContext());
        destinationLanguageCode = languageManger.getLang();
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> {
            if (!Objects.equals(sourceLanguageCode, destinationLanguageCode)) {
                if (!Objects.equals(sourceLanguageText, comment.getText().toString())) {
                    comment.setText(sourceLanguageText);
                    translateView.setText(R.string.translate);
                } else {
                    startTranslations();
                }
            }
            else{
                translateView.setText(R.string.translate);
                Toast.makeText(view.getContext(), R.string.sorry_but_this_your_language, Toast.LENGTH_SHORT).show();
            }
        }, 200);
    }

    private void startTranslations(){
        transitionOptions = new TranslatorOptions.Builder().setSourceLanguage(sourceLanguageCode).setTargetLanguage(destinationLanguageCode).build();
        translator = Translation.getClient(transitionOptions);
        DownloadConditions downloadConditions = new DownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(downloadConditions).addOnSuccessListener(unused -> {
            translateView.setText(R.string.loading);
            translator.translate(sourceLanguageText).addOnSuccessListener(s -> {
                translateView.setText(R.string.see_original);
                comment.setText(s);
            }).addOnFailureListener(e -> {
                Toast.makeText(view.getContext(), R.string.cannot_be_translated, Toast.LENGTH_SHORT).show();
                translateView.setText(R.string.translate);
                Log.e(TAG, e.getMessage());
            });
        })
        .addOnFailureListener(e -> {
            Toast.makeText(view.getContext(), R.string.cannot_be_translated, Toast.LENGTH_SHORT).show();
            translateView.setText(R.string.translate);
            Log.e(TAG, e.getMessage());
        });
    }

    private void getTextLanguageIdentifier(String text){
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(text).addOnSuccessListener(languageCode -> {
                if (!languageCode.equals("und")) {
                    sourceLanguageCode = languageCode;
                }
        });
    }



}


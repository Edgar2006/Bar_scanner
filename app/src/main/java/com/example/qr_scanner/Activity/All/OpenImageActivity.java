package com.example.qr_scanner.Activity.All;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.User.Read;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.R;
import com.squareup.picasso.Picasso;

public class OpenImageActivity extends AppCompatActivity {
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);

        imageView = findViewById(R.id.image_view);
        Intent intent = getIntent();
        if (intent != null) {
            Glide.with(OpenImageActivity.this).load(intent.getStringExtra(StaticString.url)).into(imageView);
        }

    }
}
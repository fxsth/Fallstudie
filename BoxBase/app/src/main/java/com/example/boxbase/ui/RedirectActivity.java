package com.example.boxbase.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.boxbase.R;

public class RedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect_package);
        LinearLayout delivery_to_redirect = findViewById(R.id.delivery_to_redirect);
        TextView delivery_sender = findViewById(R.id.delivery_sender);
        Intent intent = getIntent();
        delivery_sender.setText(intent.getStringExtra("sender"));

    }

}
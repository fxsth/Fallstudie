package com.example.boxbase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SendPackageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_package);
        final Button goToPaymentButton = findViewById(R.id.button_send_package_confirm);
        final Button discardButton = findViewById(R.id.button_discard);

        goToPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent goToPaymentIntent = new Intent(SendPackageActivity.this, MainMenu.class);
//                SendPackageActivity.this.startActivity(goToPaymentIntent);
                finish();
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
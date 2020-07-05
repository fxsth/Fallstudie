package com.example.boxbase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OpenCompartmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_compartment);

        LinearLayout delivery_to_redirect = findViewById(R.id.delivery_to_redirect);
        TextView delivery_sender = findViewById(R.id.delivery_sender);
        TextView delivery_destination = findViewById(R.id.delivery_destination);
        TextView delivery_status = findViewById(R.id.delivery_status);
        ImageView delivery_status_image = findViewById(R.id.delivery_status_icon);
        ImageView arrow_to_open_box = findViewById(R.id.arrow_to_open_box);
        TextView compartment_key = findViewById(R.id.compartment_key);
        Button button_finish = findViewById(R.id.button_finish);

        Intent intent = getIntent();
        delivery_sender.setText(intent.getStringExtra("sender"));
        delivery_destination.setText(intent.getStringExtra("destination"));
        delivery_status.setText(intent.getStringExtra("status"));
        delivery_status_image.setImageDrawable(OpenCompartmentActivity.this.getResources().getDrawable(intent.getIntExtra("statusImage", 0)));
        arrow_to_open_box.setVisibility(View.INVISIBLE);

        button_finish.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenCompartmentActivity.this.finish();
                    }
                }
        );
    }
}
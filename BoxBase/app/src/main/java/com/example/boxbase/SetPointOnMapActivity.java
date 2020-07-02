package com.example.boxbase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SetPointOnMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_point_on_map);

        Button button_discard = findViewById(R.id.button_discard);
        Button button_location_confirm = findViewById(R.id.button_location_confirm);
        EditText box_street = findViewById(R.id.box_street);
        EditText box_number = findViewById(R.id.box_number);
        EditText box_postcode = findViewById(R.id.box_postcode);
        EditText box_city = findViewById(R.id.box_city);

        button_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPointOnMapActivity.this.finish();
            }
        });
        button_location_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desiredAddress = box_street.getText().toString() +
                        box_number.getText().toString() +
                        ", " +
                        box_postcode.getText().toString() +
                        box_city.getText().toString();
                SetPointOnMapActivity.this.finish();
            }
        });
    }
}
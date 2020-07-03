package com.example.boxbase;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class SendPackageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    // Declaration for dropdown menus
    private static final String[] paths_day_selection = {"tomorrow", "in 2 days", "in 3 days", "in 4 days", "in 5 days"};
    private static final String[] paths_time_selection_from = {"6 am", "7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm"};
    private static final String[] paths_time_selection_to = {"7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm", "9 pm"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_package);
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


        // Function of the dropdown menus
        Spinner spinner_day_selection = (Spinner) findViewById(R.id.time_slot_selection_day);
        Spinner spinner_time_selection_from = (Spinner) findViewById(R.id.time_slot_selection_from);
        Spinner spinner_time_selection_to = (Spinner) findViewById(R.id.time_slot_selection_to);
        // day selection
        ArrayAdapter<String> adapter_day_selection = new ArrayAdapter<>(SendPackageActivity.this,
                R.layout.spinner_layout, paths_day_selection);
        adapter_day_selection.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_day_selection.setAdapter(adapter_day_selection);
        spinner_day_selection.setOnItemSelectedListener(this);
        // time "from" selection
        ArrayAdapter<String> adapter_time_selection_from = new ArrayAdapter<>(SendPackageActivity.this,
                R.layout.spinner_layout, paths_time_selection_from);
        adapter_time_selection_from.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_time_selection_from.setAdapter(adapter_time_selection_from);
        spinner_time_selection_from.setOnItemSelectedListener(this);
        // day "to" selection
        ArrayAdapter<String> adapter_time_selection_to = new ArrayAdapter<>(SendPackageActivity.this,
                R.layout.spinner_layout, paths_time_selection_to);
        adapter_time_selection_to.setDropDownViewResource(R.layout.spinner_dropdown);
        spinner_time_selection_to.setAdapter(adapter_time_selection_to);
        spinner_time_selection_to.setOnItemSelectedListener(this);
    }


    // Cases of the dropdown menus
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                // TODO Whatever you want to happen when the first item gets selected
                break;
            case 1:
                // TODO Whatever you want to happen when the second item gets selected
                break;
            case 2:
                // TODO Whatever you want to happen when the third item gets selected
                break;

        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }
}
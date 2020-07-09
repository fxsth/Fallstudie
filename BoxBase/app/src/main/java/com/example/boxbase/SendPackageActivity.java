package com.example.boxbase;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;


public class SendPackageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    // Variablen für Mutation
    String name;
    String address;
    int package_size;

    // Variablen für Timestamp
    Date von, bis;
    Spinner spinner_day_selection;
    Spinner spinner_time_selection_from;
    Spinner spinner_time_selection_to;

    // Declaration for dropdown menus
    private static final String[] paths_day_selection = {"today", "tomorrow", "in 2 days", "in 3 days", "in 4 days", "in 5 days"};
    private static final String[] paths_time_selection_from = {"6 am", "7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm"};
    private static final String[] paths_time_selection_to = {"7 am", "8 am", "9 am", "10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm", "9 pm"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_package);
        final Button goToPaymentButton = findViewById(R.id.button_send_package_confirm);
        final Button discardButton = findViewById(R.id.button_discard);
        ConstraintLayout button_size_s = findViewById(R.id.button_package_size_s);
        ConstraintLayout button_size_m = findViewById(R.id.button_package_size_m);
        ConstraintLayout button_size_l = findViewById(R.id.button_package_size_l);
        ConstraintLayout button_point_on_map = findViewById(R.id.button_point_on_map);
        ConstraintLayout button_home_address = findViewById(R.id.button_home_address);
        EditText box_receiver = findViewById(R.id.box_receiver);
        EditText box_street = findViewById(R.id.box_street);
        EditText box_number = findViewById(R.id.box_number);
        EditText box_postcode = findViewById(R.id.box_postcode);
        EditText box_city = findViewById(R.id.box_city);
        EditText box_country = findViewById(R.id.box_country);

        goToPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = box_receiver.getText().toString().trim();
                address = box_street.getText().toString().trim() + " " +
                        box_number.getText().toString().trim() + ", " +
                        box_postcode.getText().toString().trim() + " " +
                        box_city.getText().toString().trim();

                Toast.makeText(SendPackageActivity.this, "This one's for free. You're welcome!",Toast.LENGTH_LONG).show();
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


        // package size selection
        button_size_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color change
                button_size_s.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_size_m.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                button_size_l.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                // TODO: save package size s
            }
        });
        button_size_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color change
                button_size_s.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                button_size_m.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_size_l.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                // TODO: save package size m
            }
        });
        button_size_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // color change
                button_size_s.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                button_size_m.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                button_size_l.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                // TODO: save package size l
            }
        });


        //location_selection_mobile_delivery_base
        ImageView arrow_to_close_mdb_box = findViewById(R.id.arrow_to_close_mdb_box);
        ImageView arrow_to_open_mdb_box = findViewById(R.id.arrow_to_open_mdb_box);
        ConstraintLayout cl_selection_mobile_delivery_base = findViewById(R.id.cl_selection_mobile_delivery_base);
        arrow_to_close_mdb_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_selection_mobile_delivery_base.setVisibility(View.GONE);
                arrow_to_close_mdb_box.setVisibility(View.INVISIBLE);
                arrow_to_open_mdb_box.setVisibility(View.VISIBLE);
            }
        });
        arrow_to_open_mdb_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cl_selection_mobile_delivery_base.setVisibility(View.VISIBLE);
                arrow_to_close_mdb_box.setVisibility(View.VISIBLE);
                arrow_to_open_mdb_box.setVisibility(View.INVISIBLE);
            }
        });
        button_point_on_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_point_on_map.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_home_address.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                Intent SetPointOnMapIntent = new Intent(SendPackageActivity.this, SetPointOnMapActivity.class);
                SendPackageActivity.this.startActivity(SetPointOnMapIntent);
            }
        });
        button_home_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_home_address.setBackgroundResource(R.drawable.shape_button_big_primary_color_bright);
                button_point_on_map.setBackgroundResource(R.drawable.shape_button_big_primary_color_dark);
                // TODO: set up home address as desired address
            }
        });
    }


    // Cases of the dropdown menus
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Date now = Date.from(Instant.now());
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(now);
        c2.setTime(now);

        c1.add(Calendar.DATE, spinner_day_selection.getSelectedItemPosition());
        c2.add(Calendar.DATE, spinner_day_selection.getSelectedItemPosition());

        c1.set(Calendar.HOUR_OF_DAY, spinner_time_selection_from.getSelectedItemPosition()+4);   // +4 entspricht +6h und Umrechnung auf UTC
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);

        c2.set(Calendar.HOUR_OF_DAY, spinner_time_selection_to.getSelectedItemPosition()+5);
        c2.set(Calendar.MINUTE, 0);
        c2.set(Calendar.SECOND, 0);

        von = c1.getTime();
        bis = c2.getTime();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }
}
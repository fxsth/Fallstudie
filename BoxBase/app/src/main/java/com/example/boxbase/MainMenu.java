package com.example.boxbase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    List<incoming_deliveries> incoming_deliveriesList;
    ListView incoming_deliveries_ListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        final Button sendPackageButton = findViewById(R.id.button_send_package);

        sendPackageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendPackageIntent = new Intent(MainMenu.this, SendPackageActivity.class);
                MainMenu.this.startActivity(sendPackageIntent);
            }
        });

        incoming_deliveriesList = new ArrayList<>();

        /* these are just example; later entries will come from the database */

        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_home, "Amazon", "home address", "delivered"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "mobil delivery base", "ready for pick up"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_truck, "Adidas", "home address", "arrive tomorrow"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_home, "Amazon", "home address", "delivered"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "mobil delivery base", "ready for pick up"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_truck, "Adidas", "home address", "arrive tomorrow"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_home, "Amazon", "home address", "delivered"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "mobil delivery base", "ready for pick up"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_truck, "Adidas", "home address", "arrive tomorrow"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_home, "Amazon", "home address", "delivered"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_boxbase, "Zalando", "mobil delivery base", "ready for pick up"));
        incoming_deliveriesList.add(new incoming_deliveries(R.drawable.ic_delivery_status_icon_truck, "Adidas", "home address", "arrive tomorrow"));

        incoming_deliveries_ListView = findViewById(R.id.incoming_deliveries_ListView);

        incoming_deliveries_list adapter = new incoming_deliveries_list(this, R.layout.incoming_delivery_list, incoming_deliveriesList);

        incoming_deliveries_ListView.setAdapter(adapter);
    }
}
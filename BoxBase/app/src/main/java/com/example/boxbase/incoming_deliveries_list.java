package com.example.boxbase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.boxbase.ui.RedirectActivity;

import java.util.List;

public class incoming_deliveries_list extends ArrayAdapter<incoming_deliveries> {

    Context mCtx;
    int resource;
    List<incoming_deliveries> incoming_deliveriesList;
    boolean split_address = false;

    public incoming_deliveries_list(Context mCtx, int resource, List<incoming_deliveries> incoming_deliveriesList) {
        super(mCtx, resource, incoming_deliveriesList);

        this.mCtx = mCtx;
        this.resource = resource;
        this.incoming_deliveriesList = incoming_deliveriesList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view = inflater.inflate(resource, null);

        TextView delivery_sender = view.findViewById(R.id.delivery_sender);
        TextView delivery_destination = view.findViewById(R.id.delivery_destination);
        TextView delivery_status = view.findViewById(R.id.delivery_status);
        ImageView delivery_status_icon = view.findViewById(R.id.delivery_status_icon);
        Button button_delivery_details = view.findViewById(R.id.button_delivery_details);
        Button button_delivery_action = view.findViewById(R.id.button_delivery_action);
        ImageView arrow_to_open_box = view.findViewById(R.id.arrow_to_open_box);
        ImageView arrow_to_close_box = view.findViewById(R.id.arrow_to_close_box);

        incoming_deliveries incoming_deliveries = incoming_deliveriesList.get(position);
        final String address = getAddress(incoming_deliveries.getDelivery_destination(), split_address);

        delivery_sender.setText(incoming_deliveries.getDelivery_sender());
        delivery_destination.setText(address);
        delivery_status.setText(incoming_deliveries.getDelivery_status());
        delivery_status_icon.setImageDrawable(mCtx.getResources().getDrawable(incoming_deliveries.getDelivery_status_image()));

        if(delivery_status.getText().equals("pickup is being prepared") || delivery_status.getText().equals("delivered"))
        {
            button_delivery_action.setVisibility(View.GONE);
        }

        /* define what happen if the button is clicked */
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onClickView", "Position-" + position);
                if (button_delivery_details.getVisibility() == View.VISIBLE) {
                    button_delivery_details.setVisibility(View.GONE);
                    button_delivery_action.setVisibility(View.GONE);
                    arrow_to_open_box.setVisibility(View.VISIBLE);
                    arrow_to_close_box.setVisibility(View.GONE);
                } else {
                    button_delivery_details.setVisibility(View.VISIBLE);
                    button_delivery_action.setVisibility(View.VISIBLE);
                    arrow_to_open_box.setVisibility(View.GONE);
                    arrow_to_close_box.setVisibility(View.VISIBLE);
                    if(delivery_status.getText().equals("pickup is being prepared") || delivery_status.getText().equals("delivered"))
                    {
                        button_delivery_action.setVisibility(View.GONE);
                    }
                }

            }
        });

        view.findViewById(R.id.button_delivery_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailPackageIntent = new Intent(mCtx, LocateMobileDeliveryBaseActivity.class);
                detailPackageIntent.putExtra("statusImage", incoming_deliveries.getDelivery_status_image());
                detailPackageIntent.putExtra("sender", incoming_deliveries.getDelivery_sender());
                detailPackageIntent.putExtra("destination", incoming_deliveries.getDelivery_destination());
                detailPackageIntent.putExtra("status", incoming_deliveries.getDelivery_status());
                mCtx.startActivity(detailPackageIntent);
            }
        });

        view.findViewById(R.id.button_delivery_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent redirectPackageIntent = new Intent(mCtx, RedirectActivity.class);
                redirectPackageIntent.putExtra("statusImage", incoming_deliveries.getDelivery_status_image());
                redirectPackageIntent.putExtra("sender", incoming_deliveries.getDelivery_sender());
                redirectPackageIntent.putExtra("destination", incoming_deliveries.getDelivery_destination());
                redirectPackageIntent.putExtra("status", incoming_deliveries.getDelivery_status());
                mCtx.startActivity(redirectPackageIntent);
            }
        });

        return view;
    }

    private String getAddress(String address, boolean split) {
        if (split_address) {
            if (address.contains(",")) {
                int index = address.indexOf(",");
                String street = address.substring(0, index);
                String city = address.substring(index + 1, address.length());
                if (city.charAt(0) == ' ')
                    city = city.substring(1, city.length());
                return street + "\n" + city;
            }
        }
        return address;
    }
}

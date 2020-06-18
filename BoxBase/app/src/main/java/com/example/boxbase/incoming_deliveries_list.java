package com.example.boxbase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class incoming_deliveries_list extends ArrayAdapter<incoming_deliveries> {

    Context mCtx;
    int resource;
    List<incoming_deliveries> incoming_deliveriesList;

    public incoming_deliveries_list (Context mCtx, int resource, List<incoming_deliveries> incoming_deliveriesList) {
        super (mCtx, resource, incoming_deliveriesList);

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

        incoming_deliveries incoming_deliveries = incoming_deliveriesList.get(position);

        delivery_sender.setText(incoming_deliveries.getDelivery_sender());
        delivery_destination.setText(incoming_deliveries.getDelivery_destination());
        delivery_status.setText(incoming_deliveries.getDelivery_status());
        delivery_status_icon.setImageDrawable(mCtx.getResources().getDrawable(incoming_deliveries.getDelivery_status_image()));

        /* define what happen if the button is clicked */

        view.findViewById(R.id.button_delivery_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}

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

public class outgoing_deliveries_list extends ArrayAdapter<outgoing_deliveries> {

    Context mCtx;
    int resource;
    List<outgoing_deliveries> outgoing_deliveriesList;

    public outgoing_deliveries_list(Context mCtx, int resource, List<outgoing_deliveries> outgoing_deliveriesList) {
        super (mCtx, resource, outgoing_deliveriesList);

        this.mCtx = mCtx;
        this.resource = resource;
        this.outgoing_deliveriesList = outgoing_deliveriesList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        View view = inflater.inflate(resource, null);

        TextView delivery_receiver = view.findViewById(R.id.delivery_receiver);
        TextView delivery_status = view.findViewById(R.id.delivery_status);
        ImageView delivery_status_icon = view.findViewById(R.id.delivery_status_icon);

        outgoing_deliveries outgoing_deliveries = outgoing_deliveriesList.get(position);

        delivery_receiver.setText(outgoing_deliveries.getDelivery_receiver());
        delivery_status.setText(outgoing_deliveries.getDelivery_status());
        delivery_status_icon.setImageDrawable(mCtx.getResources().getDrawable(outgoing_deliveries.getDelivery_status_image()));

        /* define what happen if the button is clicked */

        view.findViewById(R.id.button_delivery_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}

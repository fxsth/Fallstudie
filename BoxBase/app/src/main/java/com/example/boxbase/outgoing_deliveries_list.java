package com.example.boxbase;

import android.content.Context;
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

import java.util.List;

public class outgoing_deliveries_list extends ArrayAdapter<outgoing_deliveries> {

    Context mCtx;
    int resource;
    List<outgoing_deliveries> outgoing_deliveriesList;

    public outgoing_deliveries_list(Context mCtx, int resource, List<outgoing_deliveries> outgoing_deliveriesList) {
        super(mCtx, resource, outgoing_deliveriesList);

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
        Button button_delivery_details = view.findViewById(R.id.button_delivery_details);
        Button button_delivery_action = view.findViewById(R.id.button_delivery_action);
        ImageView arrow_to_open_box = view.findViewById(R.id.arrow_to_open_box);
        ImageView arrow_to_close_box = view.findViewById(R.id.arrow_to_close_box);

        outgoing_deliveries outgoing_deliveries = outgoing_deliveriesList.get(position);
        delivery_receiver.setText(outgoing_deliveries.getDelivery_receiver());
        delivery_status.setText(outgoing_deliveries.getDelivery_status());
        delivery_status_icon.setImageDrawable(mCtx.getResources().getDrawable(outgoing_deliveries.getDelivery_status_image()));

        if(!delivery_status.getText().equals("ready for drop off"))
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
                    if(!delivery_status.getText().equals("ready for drop off"))
                    {
                        button_delivery_action.setVisibility(View.GONE);
                    }
                }

            }
        });
        view.findViewById(R.id.button_delivery_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        return view;
    }
}

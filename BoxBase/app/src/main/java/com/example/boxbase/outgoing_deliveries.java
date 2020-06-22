package com.example.boxbase;

public class outgoing_deliveries {

        int delivery_status_image;

        String delivery_receiver, delivery_status;

    public outgoing_deliveries(int delivery_status_image, String delivery_receiver, String delivery_status) {
        this.delivery_status_image = delivery_status_image;
        this.delivery_receiver = delivery_receiver;
        this.delivery_status = delivery_status;
    }

    public int getDelivery_status_image() {
        return delivery_status_image;
    }

    public String getDelivery_receiver() {
        return delivery_receiver;
    }

    public String getDelivery_status() {
        return delivery_status;
    }
}

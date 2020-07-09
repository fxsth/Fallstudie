package com.example.boxbase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class outgoing_deliveries {

        int delivery_status_image;
        LocalDateTime last_updated;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");

        String delivery_receiver, delivery_status;

    public outgoing_deliveries(int delivery_status_image, String delivery_receiver, String delivery_status, String last_updated) {
        this.delivery_status_image = delivery_status_image;
        this.delivery_receiver = delivery_receiver;
        this.delivery_status = delivery_status;
        this.last_updated = LocalDateTime.parse(last_updated, this.formatter);
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
    public LocalDateTime getLast_updated() {
        return last_updated;
    }

}

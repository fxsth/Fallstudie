package com.example.boxbase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class incoming_deliveries {

    int packageid;
    int delivery_status_image;
    LocalDateTime last_updated;
    String delivery_sender, delivery_destination, delivery_status;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");


    public incoming_deliveries(int packageid, int delivery_status_image, String delivery_sender, String delivery_destination, String delivery_status, String last_updated) {
        this.packageid = packageid;
        this.delivery_status_image = delivery_status_image;
        this.delivery_sender = delivery_sender;
        this.delivery_destination = delivery_destination;
        this.delivery_status = delivery_status;
        this.last_updated = LocalDateTime.parse(last_updated, this.formatter);
    }

    public int getPackageid(){return packageid;}

    public int getDelivery_status_image() {
        return delivery_status_image;
    }

    public String getDelivery_sender() {
        return delivery_sender;
    }

    public String getDelivery_destination() {
        return delivery_destination;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public LocalDateTime getLast_updated() {
        return last_updated;
    }
}

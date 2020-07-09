package com.example.boxbase;

public class incoming_deliveries {

    int packageid;
    int delivery_status_image;

    String delivery_sender, delivery_destination, delivery_status;

    public incoming_deliveries(int packageid, int delivery_status_image, String delivery_sender, String delivery_destination, String delivery_status) {
        this.packageid = packageid;
        this.delivery_status_image = delivery_status_image;
        this.delivery_sender = delivery_sender;
        this.delivery_destination = delivery_destination;
        this.delivery_status = delivery_status;
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
}

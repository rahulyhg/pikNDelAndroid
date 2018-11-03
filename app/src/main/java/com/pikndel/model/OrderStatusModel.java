package com.pikndel.model;

/**
 * Created by Sujeet on 03-05-2016.
 */
public class OrderStatusModel {
    String tvPickupCity;

    public String getAddress_to() {
        return address_to;
    }

    public void setAddress_to(String address_to) {
        this.address_to = address_to;
    }

    public String getAddress_from() {
        return tvPickupCity;
    }

    public void setAddress_from(String address_from) {
        this.tvPickupCity = address_from;
    }

    String address_to;
}

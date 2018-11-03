package com.pikndel.model;

/**
 * Created by Sujeet on 02-05-2016.
 */
public class DeliveryModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    String name,address;
    boolean isSelected;
}

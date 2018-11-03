package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by govind_gautam on 2/5/16.
 */
public class FavoriteLocationModel implements Serializable{

    String nameLocation;
    String addressLocation;

    public String getNameLocation() {
        return nameLocation;
    }

    public void setNameLocation(String nameLocation) {
        this.nameLocation = nameLocation;
    }

    public String getAddressLocation() {
        return addressLocation;
    }

    public void setAddressLocation(String addressLocation) {
        this.addressLocation = addressLocation;
    }
}

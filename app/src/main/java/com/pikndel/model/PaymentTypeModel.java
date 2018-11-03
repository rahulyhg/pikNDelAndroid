package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by govind_gautam on 4/5/16.
 */
public class PaymentTypeModel implements Serializable {

    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

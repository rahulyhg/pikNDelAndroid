package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class DeliveryInfo implements Serializable{
    public String startCityName;
    public String endCityName;
    public String startCityId;
    public String endCityId;
    public String productTypeId;
    public String packageWeightId;
    public String packageWeight;
    public String codAmount;
    public String pickupLocation;
    public String deliverylocation;
    public String pickupPartialLocation;
    public String deliveryPartiallocation;
    public String isMultiLocation;
    public String locationCount;
    public String isRoundTrip;
    public String pincode;
}

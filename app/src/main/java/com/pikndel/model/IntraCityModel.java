package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class IntraCityModel implements Serializable{

    //***new Variables***//
    public String planId;
    public String deliveryType;
    public String instructionToRider;
    public String finalCost;
    public String paymentMode;
    public String picture;
    public String userId;

    public DeliveryInfo deliveryInfo;
    public WaysToPikndelInfo waysToPikndelInfo;
    public PickupAddress pickupAddress;
    public DeliveryAddress deliveryAddress;

    /**extra required variable**/
    public String distanceKm;
    public boolean roundTrip;
    public String packageType;
    public String packageWeightAbove;
    public String weight;

    public String pickUpReference;
    public String deliveryReference;
//    public int packageType;
}

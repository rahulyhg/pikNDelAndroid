package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class OrderList implements Serializable{

    public double orderId;
    public double cityIdFrom;
    public double cityIdTo;
    public double pickUpAddressId;
    public double deliveryAddressId;
    public double packageWeightId;
    public double packagePercentage;
    public double isPlanActive;
    public double endCityId;
    public double startCityId;
    public double distanceKm;
    public double userId;
    public double cityId;
    public double productTypeId;
    public double packageWeight;
    public double codAmount;
    public double routeMgmntId;
    public double baseRate;
    public double balance;
    public double favouriteId;
    public double fromCityId;
    public double toCityId;
    public double packageTypeId;
    public double packageAmount;

    public boolean isPickingNeeded;
    public boolean isRoundTrip;
    public boolean multipleLocation;

    public String referenceId;
    public String specialRequest;
    public String image;
    public String status;
    public String information;
    public String orderType;
    public String fromCity;
    public String toCity;
    public String section;
    public String referenceNumber;
    public String packageType;
    public String pickUpLocation;
    public String deliveryLocation;
    public String deliveryByTime;
    public String deliveryByDate;
    public String productTypeName;
    public String billingType;
    public float finalAmount = 0.0f;

    public DeliveryAddress deliveryAddress;
    public PickupAddress pickupAddress;

}

/*
"waysToPickNdDel": null,
        "instructionToRider": null,
        "specialRequest": "dasfaf",
        "pickUpLocation": null,
        "deliveryLocation": null,
        "packageWeightId": 0,
        "packageWeightAbove": null,
        "packagePercentage": 0,
        "status": 0,
        "planId": null,
        "isPlanActive": 0,
        "codPlanId": null,
        "endCityId": 0,
        "startCityId": 0,
        "distanceKm": 0,
        "userId": 0,
        "city": null,
        "cityId": 0,
        "productTypeId": 0,
        "packageWeight": 0,
        "codAmount": 0,
        "routeMgmntId": 0,
        "baseRate": 0,
        "balance": 0,
        "favouriteId": 0,
        "fromCityId": 1,
        "toCityId": 1,
        "cod": 0,
        "orderId": 1,
        "fromCity": "delhi",
        "toCity": "delhi",
        "information": "hnxcghnbxc",
        "packageType": null,
        "packageTypeId": 0,
        "roundTrip": 0,
        "packageAmount": 0,
        "superSonicPercentage": 0,
        "codPlanPercentage": 0,
        "codPlanMinValue": 0,
        "boltPercentage": 0,
        "pocketFriendlyPercentage": 0,
        "intracityPlanId": null,
        "productPercentage": 0,
        "intercityPlanId": null,
        "referenceNumber":*/

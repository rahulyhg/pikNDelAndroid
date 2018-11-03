package com.pikndel.model;

import java.io.Serializable;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class RouteInfoModel implements Serializable{
    public String packageTypeId;
    public String packageTypeName;
    public String deliveryByTime;
    public String deliveryByDate;
    public float pocketFriendlyCost = 0.0f;
    public float boltCost = 0.0f;
    public float superSonicCost = 0.0f;
    public DeliveryCost deliveryCost;
}
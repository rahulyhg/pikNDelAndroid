package com.pikndel.services;

import com.pikndel.model.DeliveryAddressRequest;
import com.pikndel.model.DeliveryRequestInfo;
import com.pikndel.model.PickupAddressRequest;
import com.pikndel.model.WaysToPikndelRequestInfo;

import java.io.Serializable;

/**
 * Created by abhishek.tiwari on 7/9/15.
 */
public class ServiceRequest implements Serializable {
    public String name = "name";
    public String phoneNumber = "phoneNumber";
    public String email = "email";
    public String password = "password";
     public String maxWeightValue = "maxWeightValue";
    public String userId = "userId";
    public String otp = "otp";
    public String deviceType = "deviceType";
    public String androidDeviceKey = "androidDeviceKey";
    public String deviceToken = "deviceToken";
    public String socialId = "socialId";
    public String cityId = "cityId";
    public String location = "location";
    public String profileImage = "profileImage";
    public String contactNo = "contactNo";
    public String houseNo = "houseNo";
    public String floor = "floor";
    public String landMark = "landMark";
    public String area = "area";
    public String city = "city";
    public String pinCode = "pinCode";

    public String startCityId = "startCityId";
    public String endCityId = "endCityId";
    public String productTypeId = "productTypeId";
    public String packageWeight = "packageWeight";
    public String codAmount = "codAmount";
    public String planId = "planId";

    public String pickUpLocation = "pickUpLocation";
    public String deliveryLocation = "deliveryLocation";
    public String packageType = "packageType";
    public String packageTypeId = "packageTypeId";
    public String packageWeightId = "packageWeightId";
    public String packageWeightAbove = "packageWeightAbove";
    public String distanceKm = "distanceKm";
    public String roundTrip = "roundTrip";
    public String coupenCode = "coupenCode";
    public String deliveryType = "deliveryType";
    public String instructionToRider = "instructionToRider";
    public String finalCost = "finalCost";
    public String paymentMode = "paymentMode";
    public String picture = "picture";
    public String transactionId = "transactionId";
    public String coupenId = "coupenId";
    /**new Var**/

    public String startCityName = "startCityName";
    public String endCityName = "endCityName";
    //public String startCityId = "startCityId";
    //public String endCityId = "endCityId";
    //public String productTypeId = "productTypeId";
    //public String packageWeightId = "packageWeightId";
    //public String packageWeight = "packageWeight";
    //public String codAmount = "codAmount";
    public String pickupLocation = "pickupLocation";
    public String deliverylocation = "deliverylocation";
    public String isMultiLocation = "isMultiLocation";
    public String locationCount = "locationCount";
    public String isRoundTrip = "isRoundTrip";

    /**new Var**/
    //public String packageTypeId = "packageTypeId";
    public String packageTypeName = "packageTypeName";
    public String deliveryByTime = "deliveryByTime";
    public String deliveryByDate = "deliveryByDate";
    public String deliveryCost = "deliveryCost";

    /**new Var**/
    public String contactName = "contactName";
    public String contactNumber = "contactNumber";
    public String houseNumber = "houseNumber";
    //public String floor = "floor";
    public String landmark = "landMark";
    //public String area = "area";
    //public String cityId = "cityId";
    public String pincode = "pinCode";
    public String isFavourite = "isFavourite";
    public String cityName = "cityName";


    public String pickUpCityName = "pickUpCityName";
    public String deliveryCityName = "deliveryCityName";
    public String productType = "packageTypeName";



    public DeliveryRequestInfo deliveryInfo;
    public WaysToPikndelRequestInfo waysToPikndelInfo;
    public PickupAddressRequest pickupAddress;
    public DeliveryAddressRequest deliveryAddress;

}
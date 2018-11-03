package com.pikndel.services;

/**
 * Created by abhishek.tiwari on 11/3/16.
 */

public class RequestURL {

    //staging base url
//   public static final String BASE_URL = "http://54.83.7.62:8080/pickndel/";
   public static final String BASE_URL = "http://ec2-35-154-11-105.ap-south-1.compute.amazonaws.com:8080/pikndel/";

    //local base url
  //  public static final String BASE_URL = "http://172.16.5.16:8080/pickndel/";

    public static final String GOOGLE_DISTANCE_URL = "http://maps.googleapis.com/maps/api/directions/json?origin="+""+"&destination="+""+"&sensor=false&mode=driving";

    //production url
    //public static final String BASE_URL = "";

    public static final String POST="POST";
    public static final String PUT="PUT";
    public static final String GET="GET";
    public static final String DELETE="DELETE";
    public static final String UTF_8="UTF-8";
    public static final String APPLICATION_JSON = "application/json";
    public static final String SUCCESS_CODE = "200";
    public static final String FAILURE_CODE_400 = "400";
    public static final String FAILURE_CODE_201 = "201";
    public static final String FAILURE_CODE_202 = "202";
    public static final String FAILURE_CODE_203 = "203";
    public static final String FAILURE_CODE_204 = "204";
    public static final String FAILURE_CODE_205 = "205";
    public static final String FAILURE_CODE_206 = "206";
    public static final String FAILURE_CODE_207 = "207";
    public static final String FAILURE_CODE_208 = "208";

    public static final String URL_GET_SPLASH_SCREEN_IMAGES = BASE_URL + "getSplashScreenImages";
    public static final String URL_GET_T_AND_C = BASE_URL + "getTandC";
    public static final String URL_WALLET_BALANCE = BASE_URL + "walletBalance/%1$s";
    public static final String URL_UPDATE_WALLET = BASE_URL + "updateWallet";
    public static final String URL_GET_ABOUT_US = BASE_URL + "getAboutUs";
    public static final String URL_RESET_PASSWORD = BASE_URL + "resetPassword";
    public static final String URL_VERIFY_OTP = BASE_URL + "verifyOtp";
    public static final String URL_VERIFY_USER_BY_OTP = BASE_URL + "verifyUserByOtp";
    public static final String URL_POST_FORGOT_PASSWORD = BASE_URL + "userForgotPassword";

    public static final String URL_FIRST_SIGN_UP = BASE_URL + "firstSignUp";
    public static final String URL_LAST_SIGN_UP = BASE_URL + "lastSignUp";
    public static final String URL_GET_PLAN_LIST = BASE_URL + "getPlanList";
    public static final String URL_SAVE_USER_PLAN = BASE_URL + "saveUserPlan/%1$s/%2$s";
    public static final String URL_GENERATE_INVOICE = BASE_URL + "generateInvoice/%1$s/%2$s";
    public static final String URL_SOCIAL_LOGIN = BASE_URL + "socialLogin";
    public static final String URL_USER_LOGIN = BASE_URL + "userLogin";
    public static final String URL_GET_USER_PLAN = BASE_URL + "getUserPlan";
    public static final String URL_INTER_CITY_DELIVERY = BASE_URL + "intercityDelivery/%1$s";
    public static final String URL_INTRA_CITY_DELIVERY = BASE_URL + "intraCityDelivery/%1$s";
    public static final String URL_GET_END_CITIES = BASE_URL + "getEndCities";
    public static final String URL_GET_USER_PROFILE = BASE_URL + "getUserProfile";
    public static final String URL_GET_PENDING_ORDER_LIST = BASE_URL + "getPendingOrderList/%1$s";
    public static final String URL_GET_COMPLETED_ORDER_LIST = BASE_URL + "getCompletedOrderList/%1$s";
    public static final String URL_RESEND_OTP = BASE_URL + "resendOtp/%1$s";
    public static final String URL_UPDATE_USER_PROFILE = BASE_URL + "updateUserProfile";
    public static final String URL_GET_LIST_OF_FAVOURITE_LOCATIONS = BASE_URL + "getListOfFavouriteLocations/%1$s";
    public static final String URL_GET_PAYMENT_HISTORY = BASE_URL + "getPaymentHistory/%1$s";
    public static final String URL_ADD_FAVOURITE_LOCATION = BASE_URL + "addFavouriteLocation";
    public static final String URL_GET_WAYS_TO_PIKNDEL = BASE_URL + "getWaysToPikndel";
    public static final String URL_BOOK_EXECUTIVE = BASE_URL + "bookExecutive";
    public static final String URL_USER_LOGOUT = BASE_URL + "userLogout";
    public static final String URL_INTRA_CITY_DELIVERY_CALCULATION = BASE_URL + "intraCityDeliveryCalculation";
    public static final String URL_USER_VALIDATE_COUPEN_CODE = BASE_URL + "userValidateCoupenCode";
    public static final String URL_USER_PLACE_ORDER = BASE_URL + "userPlaceOrder";
    public static final String URL_ENTER_MOBILE_NUMBER = BASE_URL + "emailMaxWeightData";
}

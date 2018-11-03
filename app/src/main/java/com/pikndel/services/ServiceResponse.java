package com.pikndel.services;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pikndel.model.CityInfo;
import com.pikndel.model.DataModel;
import com.pikndel.model.DeliveryCost;
import com.pikndel.model.EndCityInfo;
import com.pikndel.model.OrderList;
import com.pikndel.model.ProductInfo;
import com.pikndel.model.RouteInfoModel;
import com.pikndel.model.RoutesGoogleModel;
import com.pikndel.model.StartCityInfo;
import com.pikndel.model.UserDetail;
import com.pikndel.model.UserFavouriteLocationList;
import com.pikndel.model.UserPlanList;
import com.pikndel.model.WeightInfo;

import java.io.Serializable;
import java.util.List;

public class ServiceResponse implements Serializable{

	public String code;
	public String status;
	public String tAndCData;
	public String walletBalanceData;
	public String paymentHistoryData;
	public String aboutUsData;
	public String userId;
	public String message;
	public String otp;
	public List<String> splashScreenImages;
	public List<UserPlanList> userPlanList;
	public UserDetail userDetail;
	public String profileImage;
	public String phoneNumber;
	public String email;
	public String name;
	public String planName;
	public String planDescription;
	public String planId;
	public String location;
	public String coupenId;
	public String walletBalance;
	public String accessCode;
	public String referenceNumber;
	public float discountPercent;
	public String userName;
	public List<WeightInfo> weightInfo;
	public List<RoutesGoogleModel> routes;
	public List<PaymentHistoryList> paymentHistoryList;
	public List<StartCityInfo> startCityInfo;
	public List<ProductInfo> productInfo;
	public List<EndCityInfo> endCityInfo;
	public List<CityInfo> cityInfo;
	public List<OrderList> orderList;

	public List<GooglePlace> predictions;

	public ZipCodeGooglePlace result;
	public List<UserFavouriteLocationList> userfavouriteLocationList;
	public List<RouteInfoModel> routeInfo;
	public DeliveryCost deliveryCost;
	public List<DataModel> data;
	public String isMultiLocation;
	public float minValue;
	public float maxValue;
	public String maxWeight;


/**
 *
 */

@SerializedName("TXNID")
@Expose
private String tXNID;
	@SerializedName("BANKTXNID")
	@Expose
	private String bANKTXNID;
	@SerializedName("ORDERID")
	@Expose
	private String oRDERID;
	@SerializedName("TXNAMOUNT")
	@Expose
	private String tXNAMOUNT;
	@SerializedName("STATUS")
	@Expose
	private String sTATUS;
	@SerializedName("TXNTYPE")
	@Expose
	private String tXNTYPE;
	@SerializedName("GATEWAYNAME")
	@Expose
	private String gATEWAYNAME;
	@SerializedName("RESPCODE")
	@Expose
	private String rESPCODE;
	@SerializedName("RESPMSG")
	@Expose
	private String rESPMSG;
	@SerializedName("BANKNAME")
	@Expose
	private String bANKNAME;
	@SerializedName("MID")
	@Expose
	private String mID;
	@SerializedName("PAYMENTMODE")
	@Expose
	private String pAYMENTMODE;
	@SerializedName("REFUNDAMT")
	@Expose
	private String rEFUNDAMT;
	@SerializedName("TXNDATE")
	@Expose
	private String tXNDATE;

	public String getTXNID() {
		return tXNID;
	}

	public void setTXNID(String tXNID) {
		this.tXNID = tXNID;
	}

	public String getBANKTXNID() {
		return bANKTXNID;
	}

	public void setBANKTXNID(String bANKTXNID) {
		this.bANKTXNID = bANKTXNID;
	}

	public String getORDERID() {
		return oRDERID;
	}

	public void setORDERID(String oRDERID) {
		this.oRDERID = oRDERID;
	}

	public String getTXNAMOUNT() {
		return tXNAMOUNT;
	}

	public void setTXNAMOUNT(String tXNAMOUNT) {
		this.tXNAMOUNT = tXNAMOUNT;
	}

	public String getSTATUS() {
		return sTATUS;
	}

	public void setSTATUS(String sTATUS) {
		this.sTATUS = sTATUS;
	}

	public String getTXNTYPE() {
		return tXNTYPE;
	}

	public void setTXNTYPE(String tXNTYPE) {
		this.tXNTYPE = tXNTYPE;
	}

	public String getGATEWAYNAME() {
		return gATEWAYNAME;
	}

	public void setGATEWAYNAME(String gATEWAYNAME) {
		this.gATEWAYNAME = gATEWAYNAME;
	}

	public String getRESPCODE() {
		return rESPCODE;
	}

	public void setRESPCODE(String rESPCODE) {
		this.rESPCODE = rESPCODE;
	}

	public String getRESPMSG() {
		return rESPMSG;
	}

	public void setRESPMSG(String rESPMSG) {
		this.rESPMSG = rESPMSG;
	}

	public String getBANKNAME() {
		return bANKNAME;
	}

	public void setBANKNAME(String bANKNAME) {
		this.bANKNAME = bANKNAME;
	}

	public String getMID() {
		return mID;
	}

	public void setMID(String mID) {
		this.mID = mID;
	}

	public String getPAYMENTMODE() {
		return pAYMENTMODE;
	}

	public void setPAYMENTMODE(String pAYMENTMODE) {
		this.pAYMENTMODE = pAYMENTMODE;
	}

	public String getREFUNDAMT() {
		return rEFUNDAMT;
	}

	public void setREFUNDAMT(String rEFUNDAMT) {
		this.rEFUNDAMT = rEFUNDAMT;
	}

	public String getTXNDATE() {
		return tXNDATE;
	}

	public void setTXNDATE(String tXNDATE) {
		this.tXNDATE = tXNDATE;
	}

}

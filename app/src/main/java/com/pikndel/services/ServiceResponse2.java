package com.pikndel.services;

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

public class ServiceResponse2 implements Serializable{

	public String code;
	public String data;
	public String message;







	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTAndCData() {
		return data;
	}

	public void setTAndCData(String tAndCData) {
		this.data = tAndCData;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}











}

package com.pikndel.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pikndel.R;
import com.pikndel.activity.SearchActivity;
import com.pikndel.activity.intercity.PickUpDetailsActivity;
import com.pikndel.model.ContactsList;
import com.pikndel.model.IntraCityModel;
import com.pikndel.model.PickupAddress;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.services.WebServiceAsync;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by priya.singh on 18/5/16.
 */
public class PickUpDetailsAddressFragment extends Fragment implements TextWatcher {

    private TextView tvAddLocation, tvEnterNameError, tvEnterContactNoError, tvPincodeError,tv_Course
            , tvHouseNoError, tvFloorError, tvLandMarkError, tvAreaError, tvCityError;
    private EditText etCity, etEnterContactNo, etHouseNo, etFloor, etLandMark, etPincode;
    private TextView etArea;
    private AutoCompleteTextView etEnterName;
    private ImageView ivDropDown;
    private LinearLayout llAddressDetails, llAddress;
    private Context context;
    private boolean isVisible = false;
    private ProgressDialog progressDialog = null;
    private ArrayList<ContactsList> contactsLists = new ArrayList<>();
    private RecyclerView rvPlaces;
    private PrefsManager prefsManager;
    private ImageView ivFavorite;
    private boolean isAdded = false;
    private View view;
    private String cityName = "";
    public static final int RC_PICK_UP = 100;
    private IntraCityModel intraCityModel;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_pick_up_details_address,null);
        prefsManager = new PrefsManager(context);
        if (getArguments() != null){
            cityName = getArguments().getString("CITY_INFO", "");
            intraCityModel = (IntraCityModel) getArguments().getSerializable("modal");
        }
        findIds();
        setListeners();
        setTextAttributes();

        new ContactAsync().execute();

        if(intraCityModel.deliveryType.equalsIgnoreCase("intracity")){
            if (TextUtils.isEmpty(etPincode.getText().toString().trim()))
                callGoogleAutoCompleteAPIZipCode(intraCityModel.pickUpReference);
        }


        return view;
    }

    private void setTextAttributes() {
        etEnterName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etEnterContactNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etPincode.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tv_Course.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etHouseNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvHouseNoError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEnterNameError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvEnterContactNoError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etFloor.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvFloorError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etLandMark.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvLandMarkError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etArea.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAreaError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPincodeError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvAddLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvCityError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etCity.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        ivFavorite.setImageResource(R.mipmap.star2);

    }

    private void setListeners() {

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidate()){
                    if (prefsManager.getKeyUserType().equalsIgnoreCase("FREE_USER")) {
                        CommonUtils.showToast(context, "Register or login for this service.");
                    }else {
                        if (!isAdded) {
                            addFavouriteLocationService();
                        } else {
                            CommonUtils.showToast(context, "Already added to favorite.");
                        }
                    }
                }
            }
        });


        tvAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideSoftKeyboard(context);
                if (isValidate()){
                    PickupAddress pickupAddress = new PickupAddress();
                    pickupAddress.contactName = etEnterName.getText().toString().trim();
                    pickupAddress.contactNumber = etEnterContactNo.getText().toString().trim();
                    pickupAddress.houseNumber = etHouseNo.getText().toString().trim();
                    pickupAddress.floor = etFloor.getText().toString().trim();
                    pickupAddress.landmark = etLandMark.getText().toString().trim();
                    pickupAddress.area = etArea.getText().toString().trim();
                    pickupAddress.pincode = etPincode.getText().toString().trim();
                    CommonUtils.savePreferencesString(context,AppConstant.MOBILE_KEY,etEnterContactNo.getText().toString().trim());
                    if(isAdded){
                        pickupAddress.isFavourite = "1";
                    }else {
                        pickupAddress.isFavourite =   "0";
                    }
                    ((PickUpDetailsActivity)context).onGetPickupAddress(pickupAddress);
                }
            }
        });

        llAddressDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideSoftKeyboard(context);
                setAddressLayout(isVisible);
            }
        });

        etEnterName.addTextChangedListener(this);
        etEnterContactNo.addTextChangedListener(this);
        etPincode.addTextChangedListener(this);
        etHouseNo.addTextChangedListener(this);
        etFloor.addTextChangedListener(this);
        etLandMark.addTextChangedListener(this);



        etArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intraCityModel.deliveryType.equalsIgnoreCase("intercity")) {
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("CITY_INFO", cityName);
                    intent.putExtra("SECTION", PickUpDetailsAddressFragment.class.getSimpleName());
                    startActivityForResult(intent, RC_PICK_UP);
                }
            }
        });

    }


    private void findIds() {
        tvCityError=(TextView) view.findViewById(R.id.tvCityError);
        tv_Course=(TextView) view.findViewById(R.id.tv_Course);
        tvAddLocation=(TextView) view.findViewById(R.id.tvAddLocation);
        tvEnterNameError=(TextView) view.findViewById(R.id.tvEnterNameError);
        tvEnterContactNoError=(TextView) view.findViewById(R.id.tvEnterContactNoError);
        tvPincodeError=(TextView) view.findViewById(R.id.tvPincodeError);
        tvHouseNoError=(TextView) view.findViewById(R.id.tvHouseNoError);
        tvFloorError=(TextView) view.findViewById(R.id.tvFloorError);
        tvLandMarkError=(TextView) view.findViewById(R.id.tvLandMarkError);
        tvAreaError=(TextView) view.findViewById(R.id.tvAreaError);
        etEnterName=(AutoCompleteTextView) view.findViewById(R.id.etEnterName);
        etEnterContactNo=(EditText) view.findViewById(R.id.etEnterContactNo);
        etHouseNo=(EditText) view.findViewById(R.id.etHouseNo);
        etFloor=(EditText) view.findViewById(R.id.etFloor);
        etPincode=(EditText) view.findViewById(R.id.etPincode);

        etArea=(TextView) view.findViewById(R.id.etArea);
        etArea.setText(""+intraCityModel.deliveryInfo.pickupLocation);
//stPickUpPartialAddress=intraCityModel.deliveryInfo.pickupPartialLocation;
        etCity=(EditText) view.findViewById(R.id.etCity);
        etCity.setText(""+cityName);

        etLandMark=(EditText) view.findViewById(R.id.etLandMark);
        ivDropDown=(ImageView) view.findViewById(R.id.ivDropDown);
        ivFavorite=(ImageView) view.findViewById(R.id.ivFavorite);
        llAddressDetails=(LinearLayout) view.findViewById(R.id.llAddressDetails);
        llAddress=(LinearLayout) view.findViewById(R.id.llAddress);
        rvPlaces=(RecyclerView) view.findViewById(R.id.rvPlaces);
        rvPlaces.setLayoutManager(new LinearLayoutManager(context));

        if(intraCityModel != null && intraCityModel.pickupAddress != null){

            etEnterName.setText(intraCityModel.pickupAddress.contactName);
            etEnterContactNo.setText(intraCityModel.pickupAddress.contactNumber);
            etHouseNo.setText(intraCityModel.pickupAddress.houseNumber);
            etFloor.setText(intraCityModel.pickupAddress.floor);
            etLandMark.setText(intraCityModel.pickupAddress.landmark);
            etArea.setText(intraCityModel.pickupAddress.area);
            etPincode.setText(intraCityModel.pickupAddress.pincode);
            if(intraCityModel.pickupAddress.isFavourite.equals("1")){
                ivFavorite.setImageResource(R.mipmap.star1);
            }else {
                ivFavorite.setImageResource(R.mipmap.star2);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_UP){
            etArea.setText("Area");
            if (data != null) {
                if (!TextUtils.isEmpty(data.getStringExtra("PICK_UP_CITY"))) {
                    etArea.setText(data.getStringExtra("PICK_UP_CITY"));
                }

                String reference = data.getStringExtra("PICK_UP_DETAILS");
                if (!TextUtils.isEmpty(reference)){
                    callGoogleAutoCompleteAPIZipCode(reference);
                }

            }else {
//                CommonUtils.showToast(context, "Something went wrong!");
            }
        }
    }

    private void setAddressLayout(boolean value){
        if (!value){
            llAddress.setVisibility(View.VISIBLE);
            ivDropDown.setImageResource(R.mipmap.up_arr);
        }else {
            llAddress.setVisibility(View.GONE);
            ivDropDown.setImageResource(R.mipmap.down_arr);
        }
        isVisible = !value;
    }


    private boolean isValidate() {
        if(etEnterName.getText().toString().trim().isEmpty()){
            tvEnterNameError.setVisibility(View.VISIBLE);
            etEnterName.setBackgroundResource(R.drawable.rederror_shape);
            etEnterName.requestFocus();
            return false;
        } else if(etEnterContactNo.getText().toString().trim().isEmpty()){
            tvEnterContactNoError.setText("*Please enter contact no.");
            tvEnterContactNoError.setVisibility(View.VISIBLE);
            etEnterContactNo.setBackgroundResource(R.drawable.rederror_shape);
            etEnterContactNo.requestFocus();
            return false;
        }
      /*  else if(etEnterContactNo.getText().toString().trim().length()<20){
            tvEnterContactNoError.setText("*Contact no. should not be less than 10 characters.");
            tvEnterContactNoError.setVisibility(View.VISIBLE);
            etEnterContactNo.setBackgroundResource(R.drawable.rederror_shape);
            etEnterContactNo.requestFocus();
            return false;


        }*/ else if((llAddress.getVisibility() == View.GONE)){
            CommonUtils.showToast(context, "Please enter address details.");
            return false;
        }else if((llAddress.getVisibility() == View.VISIBLE) && etHouseNo.getText().toString().trim().isEmpty()){
            tvHouseNoError.setVisibility(View.VISIBLE);
            tvAreaError.setVisibility(View.GONE);
            etHouseNo.setBackgroundResource(R.drawable.rederror_shape);
            etHouseNo.requestFocus();
            return false;

        } else if((llAddress.getVisibility() == View.VISIBLE) && etArea.getText().toString().trim().isEmpty()){
            tvAreaError.setVisibility(View.VISIBLE);
            etArea.setBackgroundResource(R.drawable.rederror_shape);
            tvAreaError.setText("*Please select area.");
            etArea.requestFocus();
            return false;
        }else if((llAddress.getVisibility() == View.VISIBLE) && etCity.getText().toString().trim().isEmpty()){
            tvCityError.setVisibility(View.VISIBLE);
            etCity.setBackgroundResource(R.drawable.rederror_shape);
            tvCityError.setText("Unalbe to fetch city. Please select nearby area.");
            tvAreaError.setVisibility(View.GONE);
            return false;
        }else if((llAddress.getVisibility() == View.VISIBLE) && etPincode.getText().toString().trim().isEmpty()){
            tvPincodeError.setText("*Please enter pincode.");
            tvPincodeError.setVisibility(View.VISIBLE);
            etPincode.setBackground(getResources().getDrawable(R.drawable.rederror_shape));
            tvAreaError.setVisibility(View.GONE);
            etPincode.requestFocus();
            return false;
        } else if((llAddress.getVisibility() == View.VISIBLE) && etPincode.getText().toString().trim().length()<6){
            tvPincodeError.setText("*Pincode should not be less than 6 characters.");
            tvPincodeError.setVisibility(View.VISIBLE);
            etPincode.setBackground(getResources().getDrawable(R.drawable.rederror_shape));
            tvAreaError.setVisibility(View.GONE);
            etPincode.requestFocus();
            return false;
        } else {

            return true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(etEnterName.getText().toString().trim().length()>0){
            tvEnterNameError.setVisibility(View.GONE);
            etEnterName.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }
        if(etEnterContactNo.getText().toString().trim().length()>0){
            tvEnterContactNoError.setVisibility(View.GONE);
            etEnterContactNo.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }
        if(etPincode.getText().toString().trim().length()>0){
            tvPincodeError.setVisibility(View.GONE);
            etPincode.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }
        if(etHouseNo.getText().toString().trim().length()>0){
            tvHouseNoError.setVisibility(View.GONE);
            etHouseNo.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }

    /*    if(etFloor.getText().toString().trim().length()>0){
            tvFloorError.setVisibility(View.GONE);
            etFloor.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }
        if(etLandMark.getText().toString().trim().length()>0){
            tvLandMarkError.setVisibility(View.GONE);
            etLandMark.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }*/

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class ContactAsync extends AsyncTask<Void, Void, ArrayList<ContactsList>> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "", context.getString(R.string.pls_wait));
        }

        @Override
        protected ArrayList<ContactsList> doInBackground(Void... params) {
            return getContactNames();
        }

        @Override
        protected void onPostExecute(ArrayList<ContactsList> result) {
            if (progressDialog != null)
                progressDialog.dismiss();

            if (result != null) {
                if (result.size() > 0) {
                    contactsLists = result;
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_item, contactsName);
                    etEnterName.setThreshold(1);
                    etEnterName.setAdapter(adapter);

                    etEnterName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            etEnterName.setText(adapter.getItem(position));
                            for (int i = 0; i < contactsLists.size(); i++) {
                                if (adapter.getItem(position).equalsIgnoreCase(contactsLists.get(i).contactName)){
                                    etEnterContactNo.setText(TextUtils.isEmpty(contactsLists.get(i).contactNumber)?"":contactsLists.get(i).contactNumber.replaceAll("[^0-9]", ""));
                                    break;
                                }
                            }
                        }
                    });
                }
            }
        }
    }


    ArrayList<String> contactsName = new ArrayList<>();
    public ArrayList<ContactsList> getContactNames() {
        contactsName.clear();
        contactsLists.clear();

        ArrayList<ContactsList> contactsLists = new ArrayList<>();
        try {
            Cursor cur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            ContactsList contactsModel;
            while (cur.moveToNext()) {
                //to get the contact names
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    contactsModel = new ContactsList();
                    String contactName = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contactsModel.contactName = TextUtils.isEmpty(contactName) ? "" : contactName;
                    String contactNumber = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    if (contactNumber.startsWith("0")) {
                        contactNumber = contactNumber.replaceFirst("0", "");
                    }

                    if (contactNumber.startsWith("+91")) {
                        contactNumber = contactNumber.replace("+91", "");
                    }

                    contactsModel.contactNumber = TextUtils.isEmpty(contactNumber) ? "" : contactNumber;
                    contactsLists.add(contactsModel);
                    contactsName.add(contactName);
                }
            }
            cur.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return contactsLists;
    }


    private void addFavouriteLocationService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest=new ServiceRequest();
            jsonObject.put(serviceRequest.userId,prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.name,etEnterName.getText().toString().trim());
            jsonObject.put(serviceRequest.contactNo,etEnterContactNo.getText().toString().trim());
            jsonObject.put(serviceRequest.houseNo,etHouseNo.getText().toString().trim());
            jsonObject.put(serviceRequest.floor,etFloor.getText().toString().trim());
            jsonObject.put(serviceRequest.landMark,etLandMark.getText().toString().trim());
            jsonObject.put(serviceRequest.area,etArea.getText().toString().trim());
            jsonObject.put(serviceRequest.city,etCity.getText().toString().trim());
            jsonObject.put(serviceRequest.pinCode, etPincode.getText().toString().trim());

            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("AddFavouriteLocationActivity", "_____Request_____" + RequestURL.URL_ADD_FAVOURITE_LOCATION);
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_ADD_FAVOURITE_LOCATION, RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    CommonUtils.showToast(context, serviceResponse.message);
                                    ivFavorite.setImageResource(R.mipmap.star1);
                                    isAdded = !isAdded;
                                } else {
                                    CommonUtils.showToast(context, serviceResponse.message);
                                }
                            } else {
                                CommonUtils.showToast(context, context.getString(R.string.server_error));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Object o) {
                        ServiceResponse serviceResponse = (ServiceResponse) o;
                        if (serviceResponse != null) {
                            CommonUtils.showToast(context, serviceResponse.message);
                        }

                    }
                }).execute();
            } else {
                CommonUtils.showToast(context, context.getString(R.string.pls_check_your_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGoogleAutoCompleteAPIZipCode(String reference){
        String url = "https://maps.googleapis.com/maps/api/place/details/json?reference="+reference+"&key="+ AppConstant.GOOGLE_KEY;
        Log.e("sf", "google" + url);

        WebServiceAsync serviceAsync = new WebServiceAsync(context, false, "", url, "GET", new ServiceStatus() {
            @Override
            public void onSuccess(Object o) {
                try {
                    ServiceResponse response = new Gson().fromJson(o.toString(),ServiceResponse.class);
                    if (response != null){
                        if (!TextUtils.isEmpty(response.status)) {
                            if (response.status.equalsIgnoreCase("OK")) {
                                if (response.result != null) {

                                    if (response.result.address_components != null && response.result.address_components.size() >0){
                                        for (int i = 0; i < response.result.address_components.size(); i++) {
                                            if (response.result.address_components.get(i).types.get(0).contains("postal")){
                                                intraCityModel.deliveryInfo.pincode = response.result.address_components.get(i).long_name;
                                                etPincode.setText(intraCityModel.deliveryInfo.pincode);
                                            }
                                        }
                                    }
                                }
                            } else {
//                                CommonUtils.showToast(context, "Something went wrong!");
                            }
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Object o) {

            }
        });

        serviceAsync.execute("");
    }
}

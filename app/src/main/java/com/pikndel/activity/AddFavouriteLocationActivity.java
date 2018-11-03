package com.pikndel.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.pikndel.model.ContactsList;
import com.pikndel.model.Terms;
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
import java.util.List;

public class  AddFavouriteLocationActivity extends AppCompatActivity implements TextWatcher {

    private TextView tvHeader, tvAddLocation, tvEnterNameError, tvEnterContactNoError, tvPincodeError,tv_Course
            , tvHouseNoError, tvFloorError, tvLandMarkError, tvAreaError, tvCityError;
    private EditText etCity, etEnterContactNo, etHouseNo, etFloor, etLandMark, etPincode;
    private TextView etArea;
    private AutoCompleteTextView etEnterName;
    private ImageView ivLeft, ivRight, ivDropDown;
    private LinearLayout llAddressDetails, llAddress;
    private Context context;
    private boolean isVisible = false;
    private ProgressDialog progressDialog = null;
    private ArrayList<ContactsList> contactsLists = new ArrayList<>();
    private RecyclerView rvPlaces;
    private PrefsManager prefsManager;

    public static final int ADD_SEARCH=200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favourite_location);
        context=AddFavouriteLocationActivity.this;

        prefsManager = new PrefsManager(context);
        findIds();
        setListeners();
        setTextAttributes();
        new ContactAsync().execute();

    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
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

        tvHeader.setText(R.string.add_favourite_location);
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
    }

    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setUpCall(context);
            }
        });

        tvAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideSoftKeyboard(context);
                if (isValidate()){
                    addFavouriteLocationService();
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
                Intent intent = new Intent(context, FavLocationSearchActivity.class);
                startActivityForResult(intent, ADD_SEARCH);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_SEARCH){
            etArea.setText("Area");
            if (data != null) {
                if (!TextUtils.isEmpty(data.getStringExtra("FAV_DESC"))) {
                    etArea.setText(data.getStringExtra("FAV_DESC"));
                }



                List<Terms> terms = new ArrayList<>();
                terms = (List<Terms>) data.getSerializableExtra("FAV_TERMS");
                Log.e("Terms_size",""+terms.size());
                if (terms.size()==2){

                    if(etArea.getText().toString().contains("Gurugram")||etArea.getText().toString().contains("Faridabad,haryana")|| etArea.getText().toString().contains("Ghaziabad")
                            ||etArea.getText().toString().contains("Ballabgarh")
                            ||etArea.getText().toString().contains("Greater Noida")||etArea.getText().toString().contains("Noida")||etArea.getText().toString().contains("Sonipat")
                            ||etArea.getText().toString().contains("Bahadurgarh")
                            ||etArea.getText().toString().contains("Anand Vihar,New delhi")){
                        etCity.setText("Delhi/NCR");
                    }else {
                        etCity.setText(TextFonts.setValidText(terms.get(0).value));
                    }



                }else if (terms.size()>=3){
                    if(etArea.getText().toString().contains("Gurugram")||etArea.getText().toString().contains("Faridabad,Haryana")|| etArea.getText().toString().contains("Ghaziabad")
                            ||etArea.getText().toString().contains("Ballabgarh")
                            ||etArea.getText().toString().contains("Greater Noida")||etArea.getText().toString().contains("Noida")||etArea.getText().toString().contains("Sonipat")
                            ||etArea.getText().toString().contains("Bahadurgarh")
                            ||etArea.getText().toString().contains("Anand Vihar,New Delhi")){

                        etCity.setText("Delhi/NCR");
                    }else {
                        etCity.setText(TextFonts.setValidText(terms.get(terms.size() - 3).value));
                    }

                }else {
                    CommonUtils.showToast(context, "Unable to fetch city.");
                }






                String reference = data.getStringExtra("FAV_REF");
                if (!TextUtils.isEmpty(reference)){
                    callGoogleAutoCompleteAPIZipCode(reference);
                }
            }else {
                CommonUtils.showToast(context, "Something went wrong!");
            }
        }
    }

    private void findIds() {
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        tvCityError=(TextView) findViewById(R.id.tvCityError);
        tv_Course=(TextView) findViewById(R.id.tv_Course);
        tvAddLocation=(TextView) findViewById(R.id.tvAddLocation);
        tvEnterNameError=(TextView) findViewById(R.id.tvEnterNameError);
        tvEnterContactNoError=(TextView) findViewById(R.id.tvEnterContactNoError);
        tvPincodeError=(TextView) findViewById(R.id.tvPincodeError);
        tvHouseNoError=(TextView) findViewById(R.id.tvHouseNoError);
        tvFloorError=(TextView) findViewById(R.id.tvFloorError);
        tvLandMarkError=(TextView) findViewById(R.id.tvLandMarkError);
        tvAreaError=(TextView) findViewById(R.id.tvAreaError);
        etEnterName=(AutoCompleteTextView) findViewById(R.id.etEnterName);
        etEnterContactNo=(EditText) findViewById(R.id.etEnterContactNo);
        etHouseNo=(EditText) findViewById(R.id.etHouseNo);
        etFloor=(EditText) findViewById(R.id.etFloor);
        etPincode=(EditText) findViewById(R.id.etPincode);

        etArea=(TextView) findViewById(R.id.etArea);

        etCity=(EditText) findViewById(R.id.etCity);
        etLandMark=(EditText) findViewById(R.id.etLandMark);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivDropDown=(ImageView) findViewById(R.id.ivDropDown);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        llAddressDetails=(LinearLayout) findViewById(R.id.llAddressDetails);
        llAddress=(LinearLayout) findViewById(R.id.llAddress);
        rvPlaces=(RecyclerView) findViewById(R.id.rvPlaces);
        rvPlaces.setLayoutManager(new LinearLayoutManager(context));
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
        } else if(etEnterContactNo.getText().toString().trim().length()<10){
            tvEnterContactNoError.setText("*Contact no. should not be less than 10 characters.");
            tvEnterContactNoError.setVisibility(View.VISIBLE);
            etEnterContactNo.setBackgroundResource(R.drawable.rederror_shape);
            etEnterContactNo.requestFocus();
            return false;
        } else if((llAddress.getVisibility() == View.GONE)){
            CommonUtils.showToast(context, "Please enter address details.");
            return false;
        }else if((llAddress.getVisibility() == View.VISIBLE) && etHouseNo.getText().toString().trim().isEmpty()){
            tvHouseNoError.setVisibility(View.VISIBLE);
            etHouseNo.setBackgroundResource(R.drawable.rederror_shape);
            etHouseNo.requestFocus();
            return false;
        }else if((llAddress.getVisibility() == View.VISIBLE) && etHouseNo.getText().toString().trim().equalsIgnoreCase(".")){
            tvHouseNoError.setVisibility(View.VISIBLE);
            tvHouseNoError.setText("Please enter valid house no.");
            etHouseNo.setBackgroundResource(R.drawable.rederror_shape);
            etHouseNo.requestFocus();
            return false;
        } else if((llAddress.getVisibility() == View.VISIBLE) && etArea.getText().toString().trim().equalsIgnoreCase("Area")){
            tvAreaError.setVisibility(View.VISIBLE);
            etArea.setBackgroundResource(R.drawable.rederror_shape);
            etArea.requestFocus();
            return false;
        }else if((llAddress.getVisibility() == View.VISIBLE) && etCity.getText().toString().trim().isEmpty()){
            tvCityError.setVisibility(View.VISIBLE);
            etCity.setBackgroundResource(R.drawable.rederror_shape);
            tvCityError.setText("Unalbe to fetch city. Please select nearby area.");
            return false;
        }else if((llAddress.getVisibility() == View.VISIBLE) && etPincode.getText().toString().trim().isEmpty()){
            tvPincodeError.setText("*Please enter pincode.");
            tvPincodeError.setVisibility(View.VISIBLE);
            etPincode.setBackground(getResources().getDrawable(R.drawable.rederror_shape));
            etPincode.requestFocus();
            return false;
        } else if((llAddress.getVisibility() == View.VISIBLE) && etPincode.getText().toString().trim().length()<6){
            tvPincodeError.setText("*Pincode should not be less than 6 characters.");
            tvPincodeError.setVisibility(View.VISIBLE);
            etPincode.setBackground(getResources().getDrawable(R.drawable.rederror_shape));
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
        if(etFloor.getText().toString().trim().length()>0){
            tvFloorError.setVisibility(View.GONE);
            etFloor.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }
        if(etLandMark.getText().toString().trim().length()>0){
            tvLandMarkError.setVisibility(View.GONE);
            etLandMark.setBackgroundResource(R.drawable.rounded_bg_edittext);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void callGoogleAutoCompleteAPIZipCode(String reference){
        String url = "https://maps.googleapis.com/maps/api/place/details/json?reference="+reference+"&key="+ AppConstant.GOOGLE_KEY + "&components=country:in";
        Log.e("sf", "google" + url);

        WebServiceAsync serviceAsync = new WebServiceAsync(context, true, "", url, "GET", new ServiceStatus() {
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
                                                etPincode.setText(response.result.address_components.get(i).long_name);
                                            }
                                        }
                                    }
                                }
                            } else {
                                CommonUtils.showToast(context, "Something went wrong!");
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
            Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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
            jsonObject.put(serviceRequest.pinCode,etPincode.getText().toString().trim());

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
                                    onBackPressed();

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
}

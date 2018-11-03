package com.pikndel.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.model.IntraCityModel;
import com.pikndel.utils.TextFonts;

public class MoreInfoDetailActivity extends AppCompatActivity {
    private TextView tvDeliverAddress, tvPickAddress, tvPackageType, tvDocumentUpto, tvUpToKg,tvHeader;
    private Context context = MoreInfoDetailActivity.this;
    private ImageView ivLeft, ivRight;
    private IntraCityModel intraCityModel;
    private TextView tvPickName;
    private TextView tvDeliverName;
    private String[] s,s1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_detail);

        if (getIntent() != null && getIntent().getSerializableExtra("INTER_CITY_DELIVERY") != null){
            intraCityModel = (IntraCityModel) getIntent().getSerializableExtra("INTER_CITY_DELIVERY");
        }

        findIds();
        setTextAttributes();
        setListeners();
        settingData(intraCityModel);
    }

    private void settingData(IntraCityModel intraCityModel){
        if (intraCityModel != null){
            tvDocumentUpto.setText(TextUtils.isEmpty(intraCityModel.packageType) ? "No Data Available" : intraCityModel.packageType);
            tvUpToKg.setText(intraCityModel.deliveryInfo == null ? "0 kg" :
                    TextUtils.isEmpty(intraCityModel.weight) ? "0 kg" :
                            intraCityModel.weight);

            if(intraCityModel.deliveryType.equalsIgnoreCase("intracity")){

                s1 = intraCityModel.deliveryInfo.pickupLocation.split(",");

                tvPickName.setText(intraCityModel.deliveryInfo == null ?"No Data Available":
                        TextUtils.isEmpty(intraCityModel.deliveryInfo.pickupLocation)?"No Data Available":s1[0]);
            }else {
                tvPickName.setText(intraCityModel.deliveryInfo == null ?"No Data Available":
                        TextUtils.isEmpty(intraCityModel.deliveryInfo.startCityName)?"No Data Available":intraCityModel.deliveryInfo.startCityName);
            }
            if (intraCityModel.pickupAddress != null) {


                if(intraCityModel.deliveryType.equalsIgnoreCase("intracity")){
                    tvPickAddress.setText(new StringBuilder()
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.houseNumber) ? "" : "H.No.-" + intraCityModel.pickupAddress.houseNumber + " ")
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.floor) ? "" : "floor-" + intraCityModel.pickupAddress.floor + " ")
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.area) ? "" : s1[0] + " ")
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.cityName) ? "" : intraCityModel.pickupAddress.cityName + " ")
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.pincode) ? "" : intraCityModel.pickupAddress.pincode)
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.landmark) ? "" : " (" + intraCityModel.pickupAddress.landmark + ")  "));
                }else {
                    tvPickAddress.setText(new StringBuilder()
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.houseNumber) ? "" : "H.No.-" + intraCityModel.pickupAddress.houseNumber + " ")
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.floor) ? "" : "floor-" + intraCityModel.pickupAddress.floor + " ")
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.area) ? "" : intraCityModel.pickupAddress.area + " ")
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.cityName) ? "" : intraCityModel.pickupAddress.cityName + " ")
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.pincode) ? "" : intraCityModel.pickupAddress.pincode)
                            .append(TextUtils.isEmpty(intraCityModel.pickupAddress.landmark) ? "" : " (" + intraCityModel.pickupAddress.landmark + ")  "));
                }


            }

            if(intraCityModel.deliveryType.equalsIgnoreCase("intracity")){

                s = intraCityModel.deliveryInfo.deliverylocation.split(",");

                if (intraCityModel.deliveryInfo.isMultiLocation.equalsIgnoreCase("0")) {
                    tvDeliverName.setText(intraCityModel.deliveryInfo == null ? "No Data Available" :
                            TextUtils.isEmpty(intraCityModel.deliveryInfo.deliverylocation) ? "No Data Available" : s[0]);
                }else {
                    tvDeliverName.setText("Multiple Locations");
                }
            }else {
                tvDeliverName.setText(intraCityModel.deliveryInfo == null ?"No Data Available":
                        TextUtils.isEmpty(intraCityModel.deliveryInfo.endCityName)?"No Data Available":intraCityModel.deliveryInfo.endCityName);
            }
            if (intraCityModel.deliveryAddress != null) {

                if(intraCityModel.deliveryType.equalsIgnoreCase("intracity")){
                    tvDeliverAddress.setText(new StringBuilder()
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.houseNumber) ? "" : "H.No.-" + intraCityModel.deliveryAddress.houseNumber + " ")
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.floor) ? "" : "floor-" + intraCityModel.deliveryAddress.floor + " ")
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.area) ? "" : s[0] + " ")
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.cityName) ? "" : intraCityModel.deliveryAddress.cityName + " ")
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.pincode) ? "" : intraCityModel.deliveryAddress.pincode)
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.landmark) ? "" : " (" + intraCityModel.deliveryAddress.landmark + ")  "));
                }else {


                    tvDeliverAddress.setText(new StringBuilder()
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.houseNumber) ? "" : "H.No.-" + intraCityModel.deliveryAddress.houseNumber + " ")
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.floor) ? "" : "floor-" + intraCityModel.deliveryAddress.floor + " ")
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.area) ? "" : intraCityModel.deliveryAddress.area + " ")
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.cityName) ? "" : intraCityModel.deliveryAddress.cityName + " ")
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.pincode) ? "" : intraCityModel.deliveryAddress.pincode)
                            .append(TextUtils.isEmpty(intraCityModel.deliveryAddress.landmark) ? "" : " (" + intraCityModel.deliveryAddress.landmark + ")  "));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_out_up, 0);
    }

    private void setListeners() {
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setTextAttributes() {
        tvDeliverAddress.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPickAddress.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPackageType.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvDocumentUpto.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvUpToKg.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));

        tvHeader.setText("More Information");
        ivLeft.setVisibility(View.INVISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.cross);
    }

    private void findIds() {
        tvDeliverAddress = (TextView)findViewById(R.id.tvDeliverAddress);
        tvDeliverName = (TextView)findViewById(R.id.tvDeliverName);
        tvPickAddress = (TextView)findViewById(R.id.tvPickAddress);
        tvPackageType = (TextView)findViewById(R.id.tvPackageType);
        tvDocumentUpto = (TextView)findViewById(R.id.tvDocumentUpto);
        tvDocumentUpto = (TextView)findViewById(R.id.tvDocumentUpto);
        tvPickName = (TextView)findViewById(R.id.tvPickName);
        tvUpToKg = (TextView)findViewById(R.id.tvUpToKg);

        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        tvHeader = (TextView)findViewById(R.id.tvHeader);
    }
}

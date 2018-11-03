package com.pikndel.activity.intercity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.DeliveryActivity;
import com.pikndel.fragment.DeliveryDetailsAddressFragment;
import com.pikndel.fragment.intracityfragment.FavoriteLocationFragment;
import com.pikndel.listeners.GetDeliveryAddress;
import com.pikndel.model.DeliveryAddress;
import com.pikndel.model.IntraCityModel;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

public class DeliveryDetailsActivity extends AppCompatActivity implements GetDeliveryAddress{

    private TextView tvHeader, tvAddress, tvFavorites;
    private Context context = DeliveryDetailsActivity.this;
    private ImageView ivRight, ivLeft;
    private FrameLayout flContainer;
    private IntraCityModel intraCityModel;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up_details);

        prefsManager = new PrefsManager(context);
        if (getIntent() != null && getIntent().getSerializableExtra("INTER_CITY_DELIVERY") != null){
            intraCityModel = (IntraCityModel) getIntent().getSerializableExtra("INTER_CITY_DELIVERY");
        }
        getId();
        setListener();
        setTextAttributes();
        settingFragment(0);
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvAddress.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvFavorites.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
        tvHeader.setText("Delivery Details");
    }


    private void setListener() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(0);
            }
        });

        tvFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefsManager.getKeyUserType().equalsIgnoreCase("FREE_USER")) {
                    CommonUtils.showToast(context, "Register or login for favourite.");
                }else {
                    settingFragment(1);
                }
            }
        });
    }

    private void getId() {
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        tvAddress=(TextView) findViewById(R.id.tvAddress);
        tvFavorites=(TextView) findViewById(R.id.tvFavorites);
        flContainer=(FrameLayout)findViewById(R.id.flContainer);
    }

    private void settingFragment(int position){
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (position){
            case 0:
                tvAddress.setBackgroundResource(R.drawable.rectangle_left_rounded);
                tvFavorites.setBackgroundResource(R.drawable.white_rectangle_right_rounded);
                tvAddress.setTextColor(getResources().getColor(R.color.white));
                tvFavorites.setTextColor(getResources().getColor(R.color.colorPrimary));
                fragment = new DeliveryDetailsAddressFragment();
                bundle.putString("CITY_INFO", intraCityModel.deliveryInfo.endCityName);
                bundle.putSerializable("modal", intraCityModel);

                fragment.setArguments(bundle);
                CommonUtils.setFragment(fragment, true, DeliveryDetailsActivity.this, flContainer);
                break;
            case 1:
                tvAddress.setBackgroundResource(R.drawable.white_rectangle_left_rounded);
                tvFavorites.setBackgroundResource(R.drawable.rectangle_right_rounded);
                tvAddress.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvFavorites.setTextColor(getResources().getColor(R.color.white));
                fragment=new FavoriteLocationFragment();
                bundle.putString("CITY_INFO", intraCityModel.deliveryInfo.endCityName);
                bundle.putString("SECTION", "DeliveryDetailsActivity");
                fragment.setArguments(bundle);
                CommonUtils.setFragment(fragment, true, DeliveryDetailsActivity.this,flContainer);
                break;
        }
    }

    @Override
    public void onGetDeliveryAddress(DeliveryAddress deliveryAddress) {
        deliveryAddress.cityId = intraCityModel.deliveryInfo.endCityId;
        deliveryAddress.cityName = intraCityModel.deliveryInfo.endCityName;
        Intent intent = new Intent();
        intent.putExtra("DELIVERY_CITY", deliveryAddress);
        setResult(DeliveryActivity.RC_DELIVERY, intent);
        finish();
    }
}

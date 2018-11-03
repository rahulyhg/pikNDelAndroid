package com.pikndel.activity.intracity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.IntraCityDeliveryActivity;
import com.pikndel.fragment.MultipleLocationFragment;
import com.pikndel.fragment.SingleLocationFragment;
import com.pikndel.listeners.GetFragmentData;
import com.pikndel.model.IntraCityModel;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

public class DeliveryLocationActivity extends AppCompatActivity implements GetFragmentData{

    private TextView tvSingleLocation,tvMultipleLocation,tvHeader;
    private ImageView ivLeft, ivRight;
    private FrameLayout flContainer;
    private LinearLayout llLocation;

    private Context context=DeliveryLocationActivity.this;
    private String cityName = "";
    private IntraCityModel intraCityModel;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_location);
        CommonUtils.hideSoftKeyboard(context);
        prefsManager = new PrefsManager(context);
        if (getIntent() != null){
            cityName = getIntent().getStringExtra("CITY_INFO");
            intraCityModel = (IntraCityModel) getIntent().getSerializableExtra("INTER_CITY_DELIVERY");
        }

        findIds();
        setListeners();
        setTextAttributes();
        settingFragment(0);
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvSingleLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvMultipleLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
        tvHeader.setText(R.string.delivery_location);

        if (new PrefsManager(context).getKeyIsMultiLocation().equalsIgnoreCase("1")) {
            llLocation.setVisibility(View.VISIBLE);
        }else {
            llLocation.setVisibility(View.GONE);
        }
    }

    private void findIds() {
        tvSingleLocation=(TextView)findViewById(R.id.tvSingleLocation);
        tvMultipleLocation=(TextView)findViewById(R.id.tvMultipleLocation);
        flContainer=(FrameLayout)findViewById(R.id.flContainer);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        llLocation=(LinearLayout) findViewById(R.id.llLocation);
    }

    private void setListeners() {

        tvSingleLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(0);
            }
        });

        tvMultipleLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefsManager.getKeyUserType().equalsIgnoreCase("FREE_USER")) {
                    CommonUtils.showToast(context, "Register or login for favourite.");
                }else {
                    settingFragment(1);
                }
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.setUpCall(context);
            }
        });

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void settingFragment(int position){
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        switch (position){
            case 0:
                fragment = new SingleLocationFragment();
                bundle.putString("CITY_INFO", cityName);
                fragment.setArguments(bundle);
                tvSingleLocation.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvMultipleLocation.setTextColor(getResources().getColor(R.color.dark_grey));
                CommonUtils.setFragment(fragment, true, DeliveryLocationActivity.this, flContainer);
                break;
            case 1:
                fragment = new MultipleLocationFragment();
                bundle.putSerializable("INTER_CITY_DELIVERY", intraCityModel);
                fragment.setArguments(bundle);
                tvMultipleLocation.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvSingleLocation.setTextColor(getResources().getColor(R.color.dark_grey));
                CommonUtils.setFragment(fragment, true, DeliveryLocationActivity.this, flContainer);
                break;
        }
    }

    @Override
    public void onGetFragmentData(String data, boolean value) {
        Intent intent = new Intent();
        intent.putExtra("DELIVERY_CITY", data);
        intent.putExtra("IS_TRIM", value);
        setResult(IntraCityDeliveryActivity.RC_DELIVERY, intent);
        finish();
    }

    @Override
    public void onGetFragmentData(String data,String refData, boolean value) {
        Intent intent = new Intent();
        intent.putExtra("DELIVERY_CITY", data);
        intent.putExtra("DELIVERY_REF", refData);
        intent.putExtra("IS_TRIM", value);
        setResult(IntraCityDeliveryActivity.RC_DELIVERY, intent);
        finish();
    }
}

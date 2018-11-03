package com.pikndel.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.MoreInformationActivity;
import com.pikndel.model.IntraCityModel;
import com.pikndel.utils.TextFonts;


/**
 * Created by Sujeet on 30-04-2016.
 */
public class MultipleLocationFragment extends Fragment implements View.OnClickListener, TextWatcher {
    View view;
    EditText etLocation;
    TextView tvSubmit, tvErrorLoc;
    private Intent intent;
    private Context context;
    private IntraCityModel intraCityModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.multi_location_fragment, container, false);

        if (getArguments() != null){
            intraCityModel = (IntraCityModel) getArguments().getSerializable("INTER_CITY_DELIVERY");
        }

        setIds();
        setListener();
        setFont();

        return view;
    }

    private void setFont() {
        etLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorLoc.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvSubmit.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void setListener() {
        tvSubmit.setOnClickListener(this);
        etLocation.addTextChangedListener(this);
    }


    private void setIds() {
        etLocation =(EditText) view.findViewById(R.id.etLocation);
        tvSubmit =(TextView) view.findViewById(R.id.tvSubmit);
        tvErrorLoc =(TextView) view.findViewById(R.id.tvErrorLoc);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvSubmit:
                if(checkValidation()){
                    intraCityModel.deliveryInfo.isMultiLocation = "1";
                    intraCityModel.deliveryInfo.locationCount = etLocation.getText().toString().trim();
                    startActivity(new Intent(context, MoreInformationActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .putExtra("INTER_CITY_DELIVERY", intraCityModel));
                }

                break;
        }
    }

    private boolean checkValidation() {
        if(etLocation.getText().toString().trim().isEmpty()){
            tvErrorLoc.setText("*Location should not be blank.");
            tvErrorLoc.setVisibility(View.VISIBLE);
            etLocation.setBackgroundResource(R.drawable.drawable_white_red_stroke);
            return false;
        }
        else if(etLocation.getText().toString().trim().equalsIgnoreCase("1")){
            tvErrorLoc.setText("*Location should be greater than 1.");
            tvErrorLoc.setVisibility(View.VISIBLE);
            etLocation.setBackgroundResource(R.drawable.drawable_white_red_stroke);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!etLocation.getText().toString().trim().isEmpty()){
            tvErrorLoc.setText("*Location should not be blank.");
            tvErrorLoc.setVisibility(View.GONE);
            etLocation.setBackgroundResource(R.drawable.drawable_white_black_stroke);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}



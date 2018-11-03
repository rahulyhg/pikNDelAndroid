package com.pikndel.fragment.signupFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.SignUpActivity;
import com.pikndel.model.UserDetail;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.TextFonts;

/**
 * Created by govind_gautam on 29/4/16.
 */

public class SignupFragment1 extends Fragment{

    private LinearLayout llDone;
    private TextView txtError, txtSignup;
    private EditText etDetails;
    private Context context ;
    private View view;
    private UserDetail userDetail = new UserDetail();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_signup_page1,null);

        if (getArguments() != null && getArguments().getSerializable(AppConstant.BUNDLE_USER_NAME) != null) {

            userDetail = (UserDetail) getArguments().getSerializable(AppConstant.BUNDLE_USER_NAME);
        }

        findIds();
        setTextAttributes();
        setListeners();

        return view;
    }

    private void setTextAttributes() {
        etDetails.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        txtError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        txtSignup.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        if (userDetail != null && !TextUtils.isEmpty(userDetail.name)) {
            etDetails.setText(userDetail.name);
        }
    }

    private void setListeners() {
        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidate()){
                    userDetail.name = etDetails.getText().toString().trim();
                    ((SignUpActivity)context).onFragmentChangeListener(1, userDetail);
                }
            }
        });

        etDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etDetails.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                txtError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void findIds() {
        llDone = (LinearLayout) view.findViewById(R.id.llDone);
        etDetails = (EditText) view.findViewById(R.id.etDetails);
        txtError =(TextView) view.findViewById(R.id.txtError);
        txtSignup =(TextView) view.findViewById(R.id.txtSignup);
    }

    private boolean isValidate() {
        if (TextUtils.isEmpty(etDetails.getText().toString().trim())) {
            etDetails.setBackgroundResource(R.drawable.rectangle_rounded_error);
            txtError.setText("*Please enter name.");
            txtError.setVisibility(View.VISIBLE);
            etDetails.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}

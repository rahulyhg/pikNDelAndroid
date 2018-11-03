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
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.TextFonts;

/**
 * Created by govind_gautam on 29/4/16.
 */

public class SignupFragment3 extends Fragment {

    private LinearLayout llDone, llMobileNumber;

    private TextView txtError, txtSignup;
    private EditText etEmail;
    private Context context;
    private LinearLayout llProgress;
    private View view;
    private UserDetail userDetail = new UserDetail();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_signup_page3, null);

        CommonUtils.hideSoftKeyboard(context);
        if (getArguments() != null && getArguments().getSerializable(AppConstant.BUNDLE_USER_NAME) != null) {
            userDetail = (UserDetail) getArguments().getSerializable(AppConstant.BUNDLE_USER_NAME);
        }

        findIds();
        setTextAttributes();
        setListeners();

        return view;
    }


    private void setTextAttributes() {

        if (userDetail != null && !TextUtils.isEmpty(userDetail.email)){
            etEmail.setText(userDetail.email);
        }

        etEmail.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        txtError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        txtSignup.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void setListeners() {
        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    userDetail.email = etEmail.getText().toString().trim();
                    ((SignUpActivity) context).onFragmentChangeListener(3, userDetail);
                }
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etEmail.setBackgroundResource(R.drawable.rectangle_rounded_corner);
                txtError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void findIds() {
        txtSignup = (TextView) view.findViewById(R.id.txtSignup);
        llDone = (LinearLayout) view.findViewById(R.id.llDone);
        etEmail = (EditText) view.findViewById(R.id.etEmail);
        txtError = (TextView) view.findViewById(R.id.txtError);
        llMobileNumber = (LinearLayout) view.findViewById(R.id.llMobileNumber);
        llProgress = (LinearLayout) view.findViewById(R.id.llProgress);
    }

    private boolean checkValidation() {
        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setBackgroundResource(R.drawable.rectangle_rounded_error);
            txtError.setText("*Please enter email address.");
            txtError.setVisibility(View.VISIBLE);
            return false;
        } else if (!CommonUtils.isValidEmail(etEmail.getText().toString().trim())) {
            etEmail.setBackgroundResource(R.drawable.rectangle_rounded_error);
            txtError.setText("*Please enter valid email address.");
            txtError.setVisibility(View.VISIBLE);
            return false;
        } else {
            return true;
        }
    }
}

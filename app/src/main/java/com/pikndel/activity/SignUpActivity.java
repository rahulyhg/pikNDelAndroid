package com.pikndel.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.pikndel.R;
import com.pikndel.fragment.signupFragment.SignupFragment1;
import com.pikndel.fragment.signupFragment.SignupFragment2;
import com.pikndel.fragment.signupFragment.SignupFragment3;
import com.pikndel.fragment.signupFragment.SignupFragment4;
import com.pikndel.listeners.FragmentChangeListener;
import com.pikndel.model.UserDetail;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;


public class SignUpActivity extends AppCompatActivity implements FragmentChangeListener{

    private ImageView ivLeft,ivRight;
    private TextView tvHeader;
    private Context context = SignUpActivity.this;
    FrameLayout container;
    private Fragment fragment = null;
    private UserDetail userDetail = new UserDetail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (getIntent() != null && getIntent().getSerializableExtra("UserDetail") != null){

            userDetail = (UserDetail) getIntent().getSerializableExtra("UserDetail");

        }

        findIds();
        setTextAttributes();
        setListeners();
        settingFragment(0, userDetail);
    }


    private void findIds() {
        tvHeader = (TextView)findViewById(R.id.tvHeader);
        ivLeft = (ImageView)findViewById(R.id.ivLeft);
        ivRight = (ImageView)findViewById(R.id.ivRight);
        container = (FrameLayout)findViewById(R.id.container);
    }


    private void setTextAttributes() {

        tvHeader.setText(R.string.sign_up);
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.INVISIBLE);
        ivLeft.setImageResource(R.mipmap.back_arr);
    }


    private void setListeners() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }


    private void settingFragment(int position, UserDetail userDetail){
        Bundle bundle = new Bundle();
        switch (position){
            case 0:
                bundle.putSerializable(AppConstant.BUNDLE_USER_NAME, userDetail);
                fragment = new SignupFragment1();
                fragment.setArguments(bundle);
                CommonUtils.setFragment(fragment, false, SignUpActivity.this, container);
                break;
            case 1:
                bundle.putSerializable(AppConstant.BUNDLE_USER_NAME, userDetail);
                fragment = new SignupFragment2();
                fragment.setArguments(bundle);
                CommonUtils.setFragment(fragment, false, SignUpActivity.this, container);
                break;
            case 2:
                bundle.putSerializable(AppConstant.BUNDLE_USER_NAME, userDetail);
                fragment = new SignupFragment3();
                fragment.setArguments(bundle);
                CommonUtils.setFragment(fragment, false, SignUpActivity.this, container);
                break;
            case 3:
                bundle.putSerializable(AppConstant.BUNDLE_USER_NAME, userDetail);
                fragment = new SignupFragment4();
                fragment.setArguments(bundle);
                CommonUtils.setFragment(fragment, false, SignUpActivity.this, container);
                break;
        }
    }


    @Override
    public void onFragmentChangeListener(int position, UserDetail userDetail) {
        settingFragment(position, userDetail);
    }


    @Override
    public void onFragmentChangeListener(int position, String value) {

    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1){
            finish();
        }else {
            getSupportFragmentManager().popBackStack();
        }

    }
}

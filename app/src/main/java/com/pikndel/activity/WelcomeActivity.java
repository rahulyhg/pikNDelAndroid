package com.pikndel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.model.UserDetail;
import com.pikndel.services.ServiceResponse;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;

/**
 * Created by govind_gautam on 29/4/16.
 */

public class WelcomeActivity extends AppCompatActivity {

    private TextView tvHeader,tvWelLogin,tvWelRegis,tvWelSkip;
    private ImageView ivLeft,ivRight;
    private Context context = WelcomeActivity.this;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        prefsManager = new PrefsManager(context);
        findIds();
        setTextAttributes();
        setListeners();

    }

    private void findIds() {

        tvHeader = (TextView)findViewById(R.id.tvHeader);
        tvWelLogin = (TextView)findViewById(R.id.tvWelLogin);
        tvWelRegis = (TextView)findViewById(R.id.tvWelRegis);
        tvWelSkip = (TextView)findViewById(R.id.tvWelSkip);

        ivLeft = (ImageView)findViewById(R.id.ivLeft);
        ivRight = (ImageView)findViewById(R.id.ivRight);
    }

    private void setTextAttributes() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvWelLogin.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvWelRegis.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvWelSkip.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));

        tvHeader.setText("Welcome");
        ivLeft.setVisibility(View.INVISIBLE);
        ivRight.setVisibility(View.INVISIBLE);
    }

    private void setListeners() {

        tvWelLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefsManager.setKeyUserType("REGISTERED");
                startActivity(new Intent(context, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }
        });

        tvWelRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefsManager.setKeyUserType("REGISTERED");
                startActivity(new Intent(context, SignUpActivity.class)
                        .putExtra("UserDetail", new UserDetail())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        tvWelSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefsManager.setKeyUserType("FREE_USER");
                startActivity(new Intent(context, HomeActivity.class)
                        .putExtra(AppConstant.INTENT_USER_TYPE, "FREE_USER")
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//                CommonUtils.savePreferencesString(context, prefsManager.SKIP_KEY_USER_ID, "-1");
                prefsManager.setKeyUserId("-1");

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}

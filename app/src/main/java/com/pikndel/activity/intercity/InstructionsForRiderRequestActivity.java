package com.pikndel.activity.intercity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.DeliveryActivity;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.TextFonts;

public class InstructionsForRiderRequestActivity extends AppCompatActivity {
    private TextView tvSubmit, tvTypeHereError, tvHeader, tvDetails;
    private ImageView ivLeft, ivRight;
    private EditText etTypeHere;
    private Context context=InstructionsForRiderRequestActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_for_rider_request);

        getId();
        setListener();
        setFont();
    }

    private void setFont() {
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvDetails.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etTypeHere.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvSubmit.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvTypeHereError.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        tvHeader.setText(R.string.instructions_for_rider);
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
    }

    private void setListener() {
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidate()) {
                    Intent intent = new Intent();
                    intent.putExtra("INSTRUCTIONS", etTypeHere.getText().toString().trim());
                    setResult(DeliveryActivity.RC_INSTRUCTION, intent);
                    finish();
                }
            }
        });

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
    }

    private void getId() {
        tvSubmit=(TextView) findViewById(R.id.tvSubmit);
        tvDetails=(TextView) findViewById(R.id.tvDetails);
        tvTypeHereError=(TextView) findViewById(R.id.tvTypeHereError);
        tvHeader=(TextView) findViewById(R.id.tvHeader);

        etTypeHere=(EditText) findViewById(R.id.etTypeHere);

        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
    }

    private boolean isValidate() {
        if(etTypeHere.getText().toString().trim().isEmpty()){
            tvTypeHereError.setVisibility(View.VISIBLE);
            return false;
        }else {
            tvTypeHereError.setVisibility(View.GONE);
            return true;
        }
    }
}

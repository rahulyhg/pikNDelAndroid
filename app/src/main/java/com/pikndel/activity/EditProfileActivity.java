package com.pikndel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pikndel.R;
import com.pikndel.imageUtils.cropimage.CropImage;
import com.pikndel.model.UserDetail;
import com.pikndel.services.RequestURL;
import com.pikndel.services.ServiceAsync;
import com.pikndel.services.ServiceRequest;
import com.pikndel.services.ServiceResponse;
import com.pikndel.services.ServiceStatus;
import com.pikndel.utils.CheckPermission;
import com.pikndel.imageUtils.CircleTransform;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.LogUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.imageUtils.TakePictureUtils;
import com.pikndel.utils.TextFonts;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class EditProfileActivity extends AppCompatActivity implements TextWatcher {

    private TextView tvHeader,tvPersonalDetails;
    private ImageView ivLeft, ivRight,ivDropDown ;
    private EditText etCurrentLocation,etMobileNumber,etEmailProfile,etProfileName;
    private LinearLayout llPersonalDetails;
    private ImageView civProfile;
    private TextView tvErrorCurrentLocation,tvErrorMobileNo,tvErrorEmail;

    private Context context = EditProfileActivity.this;
    private boolean isHide = false;
    private PrefsManager prefsManager;
    private String imageName = "";
    private String imageBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        prefsManager = new PrefsManager(context);

        findIds();
        setListener();
        setTextAttributes();
        settingUserData();
    }

    private void settingUserData(){
        if (prefsManager.getKeyUserDetailModel() != null){
            UserDetail userDetail = prefsManager.getKeyUserDetailModel();
            if (TextUtils.isEmpty(userDetail.profileImage)){
                civProfile.setImageResource(R.drawable.peo_placeholder);
            }else {
                Picasso.with(context).load(userDetail.profileImage)
                        .error(R.drawable.peo_placeholder)
                        .placeholder(R.drawable.peo_placeholder)
                        .transform(new CircleTransform()).into(civProfile);
            }
            etProfileName.setText(userDetail.name);
            etEmailProfile.setText(userDetail.email);
            etMobileNumber.setText(userDetail.phoneNumber);
            etCurrentLocation.setText(userDetail.location);
        }
    }

    private void setTextAttributes() {
        tvHeader.setText("Edit Profile");
        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        etProfileName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvPersonalDetails.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etEmailProfile.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorEmail.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etMobileNumber.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorMobileNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        etCurrentLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        tvErrorCurrentLocation.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
    }

    private void setListener() {
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvPersonalDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHide){
                    tvPersonalDetails.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.up_arr,0);
                    llPersonalDetails.setVisibility(View.VISIBLE);
                }else {
                    tvPersonalDetails.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.down_arr,0);
                    llPersonalDetails.setVisibility(View.GONE);
                }
                isHide = !isHide;
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validationToSaved()){
                    updateUserProfileService();
                }
            }
        });

        civProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckPermission.checkIsMarshMallowVersion()) {
                    if (CheckPermission.checkCameraPermission(context)) {
                        selectImage();
                    } else {
                        CheckPermission.requestCameraPermission((Activity) context, CheckPermission.REQUEST_CODE_CAMERA_PERMISSION);
                    }
                }else {
                    selectImage();
                }
            }
        });

        etEmailProfile.addTextChangedListener(this);
        etCurrentLocation.addTextChangedListener(this);
        etMobileNumber.addTextChangedListener(this);
    }

    private void findIds() {
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivDropDown=(ImageView) findViewById(R.id.ivDropDown);
        ivRight=(ImageView) findViewById(R.id.ivRight);

        etEmailProfile = (EditText) findViewById(R.id.etEmailProfile);
        etMobileNumber = (EditText)findViewById(R.id.etMobileNumber);
        etCurrentLocation = (EditText) findViewById(R.id.etCurrentLocation);
        tvPersonalDetails = (TextView)findViewById(R.id.tvPersonalDetails);
        etProfileName = (EditText)findViewById(R.id.etProfileName);

        llPersonalDetails = (LinearLayout)findViewById(R.id.llPersonalDetails);

        tvErrorCurrentLocation = (TextView)findViewById(R.id.tvErrorCurrentLocation);
        tvErrorMobileNo = (TextView)findViewById(R.id.tvErrorMobileNo);
        tvErrorEmail = (TextView)findViewById(R.id.tvErrorEmail);
        civProfile = (ImageView)findViewById(R.id.civProfile);

        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.save_ic);
    }

    private boolean validationToSaved() {

        if (etProfileName.getText().toString().trim().isEmpty()){
            Toast.makeText(context,"*Please enter profile name.",Toast.LENGTH_SHORT).show();
            etProfileName.requestFocus();
            return false;
        }
        else if ((etEmailProfile.getText().toString().trim().isEmpty()) && (llPersonalDetails.getVisibility() == View.VISIBLE) ){
            tvErrorEmail.setVisibility(View.VISIBLE);
            etEmailProfile.setBackgroundResource(R.drawable.rederror_shape);
            tvErrorEmail.setText("*Please enter email id.");
            etEmailProfile.requestFocus();
            return false;
        }
        else if (!CommonUtils.isValidEmail(etEmailProfile.getText().toString().trim()) && (llPersonalDetails.getVisibility() == View.VISIBLE)){

            tvErrorEmail.setVisibility(View.VISIBLE);
            etEmailProfile.setBackgroundResource(R.drawable.rederror_shape);
            tvErrorEmail.setText("*Please enter the valid email.");
            etEmailProfile.requestFocus();
            return false;
        }
        else if (etMobileNumber.getText().toString().trim().isEmpty() && (llPersonalDetails.getVisibility() == View.VISIBLE)){
            etMobileNumber.setBackgroundResource(R.drawable.rederror_shape);
            etEmailProfile.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvErrorMobileNo.setVisibility(View.VISIBLE);
            tvErrorMobileNo.setText("*Please enter mobile no.");
            tvErrorEmail.setVisibility(View.GONE);
            etMobileNumber.requestFocus();
            return false;
        }
        else if ((etMobileNumber.getText().toString().length()<10) && (llPersonalDetails.getVisibility() == View.VISIBLE)){
            etMobileNumber.setBackgroundResource(R.drawable.rederror_shape);
            etEmailProfile.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvErrorEmail.setVisibility(View.GONE);
            tvErrorMobileNo.setVisibility(View.VISIBLE);
            tvErrorMobileNo.setText("*Please enter valid mobile number.");
            etMobileNumber.requestFocus();
            return false;
        }
       /* else if (etCurrentLocation.getTex
       t().toString().trim().isEmpty() && (llPersonalDetails.getVisibility() == View.VISIBLE)){
            etCurrentLocation.setBackgroundResource(R.drawable.rederror_shape);
            etMobileNumber.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            etEmailProfile.setBackgroundResource(R.drawable.rectangle_rounded_corner);
            tvErrorCurrentLocation.setVisibility(View.VISIBLE);
            tvErrorEmail.setVisibility(View.GONE);
            tvErrorMobileNo.setVisibility(View.GONE);
            etCurrentLocation.requestFocus();
            return false;
        }*/else {
            return true;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        allErrorGone();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void allErrorGone(){
        etCurrentLocation.setBackgroundResource(R.drawable.rectangle_rounded_corner);
        etMobileNumber.setBackgroundResource(R.drawable.rectangle_rounded_corner);
        etEmailProfile.setBackgroundResource(R.drawable.rectangle_rounded_corner);

        tvErrorEmail.setVisibility(View.GONE);
        tvErrorMobileNo.setVisibility(View.GONE);
        tvErrorCurrentLocation.setVisibility(View.GONE);
    }

    /*select image dialog box*/
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    imageName = "picture";
                    TakePictureUtils.takePicture(EditProfileActivity.this, imageName);
                } else if (items[item].equals("Choose from Library")) {
                    imageName = "picture";
                    TakePictureUtils.openGallery(EditProfileActivity.this);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TakePictureUtils.PICK_GALLERY:
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(getExternalFilesDir("temp"), imageName + ".jpg"));
                        TakePictureUtils.copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        TakePictureUtils.startCropImage(this, imageName + ".jpg");

                    } catch (Exception e) {

                        CommonUtils.showToast(context, "Error in picture!");
                    }
                    break;
                case TakePictureUtils.TAKE_PICTURE:
                    imageName = "picture";
                    TakePictureUtils.startCropImage(this, imageName + ".jpg");
                    break;
                case TakePictureUtils.CROP_FROM_CAMERA:
                    String imagePath = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (imagePath == null) {
                        CommonUtils.showToast(context, "Error in picture!");
                        return;
                    }
                    imageBase64 = CommonUtils.convertPathToBase64(imagePath);
                    Picasso.with(context).load(new File(imagePath)).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new CircleTransform()).into(civProfile);
                    break;
            }
        }
    }

    private void updateUserProfileService() {
        try {
            JSONObject jsonObject = new JSONObject();
            ServiceRequest serviceRequest = new ServiceRequest();
            jsonObject.put(serviceRequest.userId, prefsManager.getKeyUserId());
            jsonObject.put(serviceRequest.email, etEmailProfile.getText().toString().trim());
            jsonObject.put(serviceRequest.location, etCurrentLocation.getText().toString().trim());
            jsonObject.put(serviceRequest.profileImage, imageBase64);
            jsonObject.put(serviceRequest.phoneNumber, etMobileNumber.getText().toString().trim());
            jsonObject.put(serviceRequest.name, etProfileName.getText().toString().trim());
            if (CommonUtils.isOnline(context)) {
                LogUtils.infoLog("EditProfileActivity", "_____Request_____" + RequestURL.URL_UPDATE_USER_PROFILE );
                new ServiceAsync(context, true, jsonObject.toString(), RequestURL.URL_UPDATE_USER_PROFILE , RequestURL.POST, new ServiceStatus() {

                    @Override
                    public void onSuccess(Object o) {
                        try {
                            ServiceResponse serviceResponse = (ServiceResponse) o;
                            if (serviceResponse != null) {
                                if (!TextUtils.isEmpty(serviceResponse.code) && TextUtils.equals(serviceResponse.code, RequestURL.SUCCESS_CODE)) {
                                    onBackPressed();
                                    CommonUtils.showToast(context, "Your data saved successfully.");
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

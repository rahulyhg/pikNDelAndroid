
package com.pikndel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.activity.intercity.DeliveryDetailsActivity;
import com.pikndel.activity.intercity.InstructionsForRiderRequestActivity;
import com.pikndel.activity.intercity.PickUpDetailsActivity;
import com.pikndel.imageUtils.cropimage.CropImage;
import com.pikndel.model.DeliveryAddress;
import com.pikndel.model.IntraCityModel;
import com.pikndel.model.PickupAddress;
import com.pikndel.utils.CheckPermission;
import com.pikndel.imageUtils.CircleTransform;
import com.pikndel.utils.CommonUtils;
import com.pikndel.imageUtils.TakePictureUtils;
import com.pikndel.utils.PrefsManager;
import com.pikndel.utils.TextFonts;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class DeliveryActivity extends AppCompatActivity {
    LinearLayout llPickUp,llRider,llDelivery,llPicture;
    private TextView tvHeader,tvExpress, tvClickAPicture, tvPickUp, tvInstruction, tvDelivery;
    private ImageView ivLeft, ivRight,ivNext ,ivPickup;
    private ImageView ivUserImage ;

    private Context context=DeliveryActivity.this;
    private String imageName = "";
    private String imageBase64 = "";

    private IntraCityModel intraCityModel;

    public static final int RC_PICK_UP = 100;
    public static final int RC_DELIVERY = 101;
    public static final int RC_INSTRUCTION = 102;
    private boolean pickUp = false;

    private boolean isPickUp = false, isDelivery = false;
    private PrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_activity);

        prefsManager = new PrefsManager(context);
        if (getIntent() != null && getIntent().getSerializableExtra("INTER_CITY_DELIVERY") != null){
            intraCityModel = (IntraCityModel) getIntent().getSerializableExtra("INTER_CITY_DELIVERY");
        }

        findIds();
        setListeners();
        setTextAttributes();
    }

    private void setTextAttributes() {

        tvHeader.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvPickUp.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvInstruction.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvDelivery.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvClickAPicture.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
        tvExpress.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));

        tvHeader.setText("Order Detail");
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.call_us);
    }

    private Intent intent;
    private void setListeners() {
        llPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, PickUpDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("INTER_CITY_DELIVERY", intraCityModel);
                startActivityForResult(intent, RC_PICK_UP);
            }
        });

        llRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, InstructionsForRiderRequestActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, RC_INSTRUCTION);
            }
        });

        llDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, DeliveryDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("INTER_CITY_DELIVERY", intraCityModel);
                startActivityForResult(intent, RC_DELIVERY);
            }
        });

        llPicture.setOnClickListener(new View.OnClickListener() {
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


        ivNext .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefsManager.getKeyUserType().equalsIgnoreCase("FREE_USER")) {
                    if (pickUp) {
                        startActivity(new Intent(context, MoreInformationActivity.class)
                                .putExtra("INTER_CITY_DELIVERY", intraCityModel)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }

                    else {
                        CommonUtils.showToast(context, "Please enter Pick-up details.");
                    }
                }else {
                    startActivity(new Intent(context, MoreInformationActivity.class)
                            .putExtra("INTER_CITY_DELIVERY", intraCityModel)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

    private void findIds() {
        llPickUp=(LinearLayout)findViewById(R.id.llPickUp);
        llRider=(LinearLayout)findViewById(R.id.llRider);
        llDelivery=(LinearLayout)findViewById(R.id.llDelivery);
        llPicture=(LinearLayout)findViewById(R.id.llPicture);
        tvExpress=(TextView) findViewById(R.id.tvExpress);
        tvPickUp=(TextView) findViewById(R.id.tvPickUp);
        ivPickup=(ImageView) findViewById(R.id.ivPickup);
        tvDelivery=(TextView) findViewById(R.id.tvDelivery);
        tvInstruction=(TextView) findViewById(R.id.tvInstruction);
        ivNext=(ImageView) findViewById(R.id.ivNext);
        tvHeader=(TextView) findViewById(R.id.tvHeader);
        tvClickAPicture=(TextView) findViewById(R.id.tvClickAPicture);
        ivLeft=(ImageView) findViewById(R.id.ivLeft);
        ivRight=(ImageView) findViewById(R.id.ivRight);
        ivUserImage=(ImageView)findViewById(R.id.ivUserImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    imageName = "picture";
                    TakePictureUtils.takePicture(DeliveryActivity.this, imageName);
                } else if (items[item].equals("Choose from Library")) {
                    imageName = "picture";
                    TakePictureUtils.openGallery(DeliveryActivity.this);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
                if (data != null && !TextUtils.isEmpty(data.getStringExtra(CropImage.IMAGE_PATH))) {
                    String imagePath = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (imagePath == null) {
                        CommonUtils.showToast(context, "Error in picture!");
                        return;
                    }
                    imageBase64 = CommonUtils.convertPathToBase64(imagePath);
                    intraCityModel.picture = imageBase64;
                    tvClickAPicture.setTextColor(getResources().getColor(R.color.primary_dark));
                    tvClickAPicture.setTypeface(null, Typeface.BOLD);
                    Picasso.with(context).load(new File(imagePath)).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new CircleTransform()).into(ivUserImage);
                }
                break;
            case RC_PICK_UP:
                if (data != null && data.getSerializableExtra("PICK_UP_CITY") != null) {
                    pickUp = true;
                    intraCityModel.pickupAddress = (PickupAddress) data.getSerializableExtra("PICK_UP_CITY");
                    tvPickUp.setTextColor(getResources().getColor(R.color.text_dark));
                    tvPickUp.setTypeface(null, Typeface.BOLD);
                }
//                else {
//                    tvPickUp.setTextColor(getResources().getColor(R.color.dark_grey));
//                }
                break;
            case RC_DELIVERY:
                if (data != null && data.getSerializableExtra("DELIVERY_CITY") != null) {
                    intraCityModel.deliveryAddress = (DeliveryAddress) data.getSerializableExtra("DELIVERY_CITY");
                    tvDelivery.setTextColor(getResources().getColor(R.color.text_dark));
                    tvDelivery.setTypeface(null, Typeface.BOLD);
                    isDelivery = true;
                }
//                else {
//                    tvDelivery.setTextColor(getResources().getColor(R.color.dark_grey));
//                }
                break;
            case RC_INSTRUCTION:
                if (data != null && !TextUtils.isEmpty(data.getStringExtra("INSTRUCTIONS"))) {
                    intraCityModel.instructionToRider = data.getStringExtra("INSTRUCTIONS");
                    tvInstruction.setTextColor(getResources().getColor(R.color.text_dark));
                    tvInstruction.setTypeface(null, Typeface.BOLD);
                }else {
                    tvInstruction.setTextColor(getResources().getColor(R.color.dark_grey));
                }
                break;
        }
    }

}


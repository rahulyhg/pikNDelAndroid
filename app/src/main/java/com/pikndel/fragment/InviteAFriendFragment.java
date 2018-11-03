package com.pikndel.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pikndel.R;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.TextFonts;

public class InviteAFriendFragment extends Fragment implements View.OnClickListener {

    public static final String FB_MSG_PACKAGE ="com.facebook.orca";
    public static final String EMAIL_PACKAGE = "com.android.email";
    public static final String WATSAPP_PACKAGE = "com.whatsapp";

    public static final String SHARE_LINK = "Hi, Download the Pikndel app and get 10% off on your first order.Choose Pikndel as your On-Demand Delivery & Pickup and Delivery shall just be a click away. Click on _______________ ";
    private TextView tvInviteSms,tvInviteEmail,tvInviteFb,tvInviteWhatsapp;
    private View view;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_invite_friend, container, false);

        getIds();
        setListener();
        setFont();
        return view;
    }

    private void setFont() {
        tvInviteSms.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvInviteEmail.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvInviteFb.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvInviteWhatsapp.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
    }

    private void setListener() {
        tvInviteEmail.setOnClickListener(this);
        tvInviteWhatsapp.setOnClickListener(this);
        tvInviteFb.setOnClickListener(this);
        tvInviteSms.setOnClickListener(this);
    }

    private void getIds() {
        tvInviteSms=(TextView) view.findViewById(R.id.tvInviteSms);
        tvInviteEmail=(TextView) view.findViewById(R.id.tvInviteEmail);
        tvInviteFb=(TextView) view.findViewById(R.id.tvInviteFb);
        tvInviteWhatsapp=(TextView) view.findViewById(R.id.tvInviteWhatsapp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvInviteSms:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW)
                            .setType("vnd.android-dir/mms-sms")
                            .putExtra("sms_body", SHARE_LINK));
                }catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    CommonUtils.showToast(context, "Default messaging app not found.");
                }
                break;


            case R.id.tvInviteEmail:


                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);

                    String aEmailList[] = { "user@fakehost.com","user2@fakehost.com" };
                    String aEmailCCList[] = { "user3@fakehost.com","user4@fakehost.com"};
                    String aEmailBCCList[] = { "user5@fakehost.com" };


                    emailIntent.setType("plain/text");

                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, Download the Pikndel app and get 10% off on your first order.Choose Pikndel as your On-Demand Delivery & Pickup and Delivery shall just be a click away. Click on _______________ ");

                    startActivity(emailIntent);
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                    Toast.makeText(context,"Please Install Gmail/Email.", Toast.LENGTH_SHORT).show();
                }


           /*     try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messagetoshare));
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(emailIntent, "Send your email in:"));
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }

*/







/*

                try {
                    startActivity(new Intent(Intent.ACTION_SEND).setType("text/plain")
                            .setPackage(EMAIL_PACKAGE)
                            .putExtra(Intent.EXTRA_TEXT, SHARE_LINK)
                    );
                }catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    CommonUtils.showToast(context, "Email client not found.");
                }
*/







                break;


            case R.id.tvInviteFb:
                try {
                    startActivity(new Intent(Intent.ACTION_SEND)
                            .setType("text/plain")
                            .setPackage(FB_MSG_PACKAGE)
                            .putExtra(Intent.EXTRA_TEXT,SHARE_LINK));
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                    Toast.makeText(context,"Please Install Facebook Messenger.", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.tvInviteWhatsapp:
                try {
                    startActivity(new Intent(Intent.ACTION_SEND)
                            .setType("text/plain")
                            .setPackage(WATSAPP_PACKAGE)
                            .putExtra(Intent.EXTRA_TEXT,SHARE_LINK));
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "Please Install WhatsApp Messenger.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

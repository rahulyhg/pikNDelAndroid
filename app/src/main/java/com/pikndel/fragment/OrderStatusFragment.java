package com.pikndel.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.TextFonts;

public class OrderStatusFragment extends Fragment{
    private FrameLayout flContainer;
    TextView tvPending,tvCompleted;
    private Context context;
    private View view;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(context).inflate(R.layout.fragment_order_status, container, false);

        findIds();
        setListeners();
        setTextAttributes();
        settingFragment(0);

        return view;
    }

    private void setTextAttributes() {
        tvPending.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        tvCompleted.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
    }

    private void findIds() {
        tvPending=(TextView) view.findViewById(R.id.tvPending);
        tvCompleted=(TextView) view.findViewById(R.id.tvCompleted);
        flContainer=(FrameLayout) view.findViewById(R.id.flContainer);

    }
    private void setListeners() {
        tvPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(0);
            }
        });

        tvCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingFragment(1);
            }
        });
    }

    private void settingFragment(int position){
        switch (position){
            case 0:
                tvPending.setTextColor(getResources().getColor(R.color.white));
                tvCompleted.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvPending.setBackgroundResource(R.drawable.rectangle_left_rounded);
                tvCompleted.setBackgroundResource(R.drawable.white_rectangle_right_rounded);
                CommonUtils.setFragment(new PendingFragment(), true, getActivity(), flContainer);
                break;
            case 1:
                tvPending.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvCompleted.setTextColor(getResources().getColor(R.color.white));
                tvPending.setBackgroundResource(R.drawable.white_rectangle_left_rounded);
                tvCompleted.setBackgroundResource(R.drawable. rectangle_right_rounded);
                CommonUtils.setFragment(new CompletedFragment(), true, getActivity(), flContainer);
                break;
        }
    }
}

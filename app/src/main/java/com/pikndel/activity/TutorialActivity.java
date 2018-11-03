package com.pikndel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.utils.circularPageIndicator.CirclePageIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by govind_gautam on 29/4/16.
 */

public class TutorialActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private CirclePageIndicator cpIndicator;
    private ViewPager viewPager;
    private TextView tvSkip;

    private List<String> imagesList = new ArrayList<>();
    private Context context = TutorialActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        imagesList.clear();
        if (getIntent() != null && getIntent().getSerializableExtra("IMAGE_LIST") != null){
            imagesList = (List<String>) getIntent().getSerializableExtra("IMAGE_LIST");
        }

        getIDs();
        setListener();
        settingAdapter();

    }

    private void setListener() {
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, WelcomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
    }

    private void settingAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(context, imagesList);
        viewPager.setAdapter(viewPagerAdapter);
        cpIndicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == imagesList.size()-1){
                    tvSkip.setText("DONE");
                }else {
                    tvSkip.setText("SKIP");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getIDs() {
        tvSkip = (TextView) findViewById(R.id.tvSkip);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        cpIndicator = (CirclePageIndicator)findViewById(R.id.cpIndicator);
    }

    public class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private List<String> imagesList;

        public ViewPagerAdapter(Context context, List<String> imagesList) {
            this.context = context;
            this.imagesList = imagesList;
        }

        @Override
        public int getCount()

        {
            return imagesList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(final ViewGroup container, int position) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.row_tutorial, container, false);

            ImageView ivTutorial = (ImageView) itemView.findViewById(R.id.ivTutorial);
            String image = imagesList.get(position);
            if (!TextUtils.isEmpty(image)) {
                Picasso.with(context).load(image).error(R.mipmap.wlcm).placeholder(R.mipmap.wlcm).into(ivTutorial);
            }else {
                ivTutorial.setImageResource(R.mipmap.wlcm);
            }

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }
}

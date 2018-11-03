package com.pikndel.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pikndel.utils.TextFonts;

import java.util.List;

public class MySpinnerAdapter extends ArrayAdapter<String> {
    // (In reality I used a manager which caches the Typeface objects)
    // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);
    private List<String> items;
    private Context context;
    public MySpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.context=context;
        this.items=items;
    }

    // Affects default (closed) state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(items.get(position));
        view.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        return view ;
    }
    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        return view;
    }
}

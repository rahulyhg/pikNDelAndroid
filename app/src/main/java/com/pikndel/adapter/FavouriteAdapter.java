package com.pikndel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.UserFavouriteLocationList;
import com.pikndel.utils.TextFonts;

import java.util.List;

/**
 * Created by priya.singh on 17/3/16.
 */
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private List<UserFavouriteLocationList> userfavouriteLocationList;
    private Context context;
    private RecyclerItemClickListener listener;

    public FavouriteAdapter(Context context, List<UserFavouriteLocationList> userfavouriteLocationList, RecyclerItemClickListener listener) {
        this.context=context;
        this.userfavouriteLocationList=userfavouriteLocationList;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_favourite,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final UserFavouriteLocationList model=userfavouriteLocationList.get(position);

        holder.tvCompanyName.setText(new StringBuilder()
                .append(TextUtils.isEmpty(model.name)?"":model.name)
                .append(", ")
                .append(TextUtils.isEmpty(model.contactNo)?"":model.contactNo));

        holder.tvCompanyAddress.setText(new StringBuilder()
                .append(TextUtils.isEmpty(model.houseNo)? "":model.houseNo+", ")
                .append(TextUtils.isEmpty(model.floor)? "":model.floor+", ")
                .append(TextUtils.isEmpty(model.area)? "":model.area+", ")
                .append(TextUtils.isEmpty(model.city)? "":model.city+", ")
                .append(TextUtils.isEmpty(model.pinCode)? "":model.pinCode)
                .append(TextUtils.isEmpty(model.landMark)? "":" (near "+model.landMark+")"));

        //holder.tvCompanyCity.setText(TextUtils.isEmpty(model.city)?"":model.city);
        holder.tvCompanyCity.setVisibility(View.GONE);

        holder.llFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userfavouriteLocationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCompanyName,tvCompanyAddress,tvCompanyCity;

        ImageView ivCompanyRating;
        private LinearLayout llFavourite;

        public ViewHolder(View itemView) {
            super(itemView);
            ivCompanyRating=(ImageView)itemView.findViewById(R.id.ivCompanyRating);
            tvCompanyName=(TextView)itemView.findViewById(R.id.tvCompanyName);
            tvCompanyAddress =(TextView)itemView.findViewById(R.id.tvCompanyAddress);
            tvCompanyCity =(TextView)itemView.findViewById(R.id.tvCompanyCity);
            llFavourite =(LinearLayout) itemView.findViewById(R.id.llFavourite);

            tvCompanyName.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvCompanyAddress.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvCompanyCity.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));

        }
    }

}

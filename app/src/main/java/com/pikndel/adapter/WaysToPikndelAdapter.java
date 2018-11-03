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
import com.pikndel.activity.WaysToPikndelActivity;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.RouteInfoModel;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.TimeStampFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by govind_gautam on 2/5/16.
 */
public class WaysToPikndelAdapter extends RecyclerView.Adapter<WaysToPikndelAdapter.ViewHolder> {

    private List<RouteInfoModel> routeInfo = new ArrayList<>();
    private Context context;
    private RecyclerItemClickListener listener;
    private String section;

    public  WaysToPikndelAdapter(Context context, List<RouteInfoModel> routeInfo, String section, RecyclerItemClickListener listener){
        this.context = context;
        this.routeInfo = routeInfo;
        this.listener = listener;
        this.section = section;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rowView = LayoutInflater.from(context).inflate(R.layout.row_ways_pikndel,parent,false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        RouteInfoModel model = routeInfo.get(position);

        if (!TextUtils.isEmpty(model.packageTypeName)) {
            holder.tvNameProduct.setText(model.packageTypeName);
            if (model.packageTypeId.equalsIgnoreCase("2")){
                holder.ivClock.setImageResource(R.mipmap.d_pack_icon3);
                if (!section.equalsIgnoreCase("intercity")){
//                    holder.tvPrice.setText(String.format(Locale.US, "%.2f", model.boltCost));
                    holder.tvPrice.setText(""+model.boltCost);
                }else {
                    holder.tvPrice.setText(String.format(Locale.US, "%.2f", routeInfo.get(0).deliveryCost.boltCost));
                    holder.tvPrice.setText(""+routeInfo.get(0).deliveryCost.boltCost);
                }
            }else if (model.packageTypeId.equalsIgnoreCase("1")){
                holder.ivClock.setImageResource(R.mipmap.d_pack_icon2);
                if (!section.equalsIgnoreCase("intercity")){
//                    holder.tvPrice.setText(String.format(Locale.US, "%.2f", model.superSonicCost));
                    holder.tvPrice.setText(""+model.superSonicCost);
                }else {
//                    holder.tvPrice.setText(String.format(Locale.US, "%.2f", routeInfo.get(0).deliveryCost.superSonicCost));
                    holder.tvPrice.setText(""+routeInfo.get(0).deliveryCost.superSonicCost);
                }
            }else if (model.packageTypeId.equalsIgnoreCase("3")){
                holder.ivClock.setImageResource(R.mipmap.d_pack_icon1);
                if (!section.equalsIgnoreCase("intercity")){
//                    holder.tvPrice.setText(String.format(Locale.US, "%.2f", model.pocketFriendlyCost));
                    holder.tvPrice.setText(""+model.pocketFriendlyCost);
                }else {
//                    holder.tvPrice.setText(String.format(Locale.US, "%.2f", routeInfo.get(0).deliveryCost.pocketFriendlyCost));
                    holder.tvPrice.setText(""+routeInfo.get(0).deliveryCost.pocketFriendlyCost);
                }
            }else {
                holder.tvNameProduct.setText("No Data Available");
                holder.tvPrice.setText("0");
            }
        }else {
            holder.tvNameProduct.setText("No Data Available");
            holder.tvPrice.setText("0");
        }

        holder.tvDate.setText(new StringBuilder()
                .append(model.deliveryByTime)
                .append(", ")
                .append(TextUtils.isEmpty(model.deliveryByDate)?"dd MMM yyyy": TimeStampFormatter.getValueFromTS(model.deliveryByDate, "dd MMM yyyy")));

        holder.llCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WaysToPikndelActivity)context).setPrice(holder.tvPrice.getText().toString().trim());
                listener.onItemClickListener(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return routeInfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNameProduct,tvPrice,tvDate, tvDeliverBy;
        private ImageView ivClock;
        private LinearLayout llCard;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNameProduct = (TextView)itemView.findViewById(R.id.tvNameProduct);
            tvPrice = (TextView)itemView.findViewById(R.id.tvPrice);
            tvDate = (TextView)itemView.findViewById(R.id.tvDate);
            tvDeliverBy = (TextView)itemView.findViewById(R.id.tvDeliverBy);
            ivClock = (ImageView)itemView.findViewById(R.id.ivClock);
            llCard = (LinearLayout) itemView.findViewById(R.id.llCard);

            tvDeliverBy.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvNameProduct.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvPrice.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_SEMI_BOLD));
            tvDate.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        }
    }
}

package com.pikndel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.listeners.RecyclerItemClickListener;
import com.pikndel.model.CityInfo;
import com.pikndel.model.OrderList;
import com.pikndel.services.ServiceResponse;
import com.pikndel.utils.AppConstant;
import com.pikndel.utils.CommonUtils;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.TimeStampFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sujeet on 02-05-2016.
 */
public class PendingFragmentAdapter extends RecyclerView.Adapter<PendingFragmentAdapter.ViewHolder> {
    private Context context;
    private List<OrderList> orderList =new ArrayList<>();
    private RecyclerItemClickListener listener;
    private String strCityName;

    public PendingFragmentAdapter(Context context, List<OrderList> orderList, RecyclerItemClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pending_fragment, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        OrderList orderListModel = orderList.get(position);


        if (!TextUtils.isEmpty(orderListModel.pickUpLocation) && !TextUtils.isEmpty(orderListModel.deliveryLocation) ){


            holder.tvPickupCity.setText(TextUtils.isEmpty(orderListModel.pickUpLocation)?"":orderListModel.pickUpLocation);
            holder.tvDeliverCity.setText(TextUtils.isEmpty(orderListModel.deliveryLocation)?"":orderListModel.deliveryLocation);


        }
        else {

            holder.tvPickupCity.setText(TextUtils.isEmpty(orderListModel.fromCity)?"":orderListModel.fromCity);
            holder.tvDeliverCity.setText(TextUtils.isEmpty(orderListModel.toCity)?"":orderListModel.toCity);
        }

        holder.tvRefNo.setText(TextUtils.isEmpty(orderListModel.referenceNumber)?"":orderListModel.referenceNumber);
        holder.tvAmount.setText("₹ "+String.format(Locale.US, "%.2f", orderListModel.finalAmount));
        holder.tvTime.setText(new StringBuilder()
                .append(TextUtils.isEmpty(orderListModel.deliveryByDate)?"": TimeStampFormatter.getValueFromTS(orderListModel.deliveryByDate, "dd-MMM-yyyy"))
                .append(" ")
                .append(TextUtils.isEmpty(orderListModel.deliveryByTime)?"": TimeStampFormatter.changeDateTimeFormat(orderListModel.deliveryByTime, "HH:mm:ss", "HH:mm"))
                .append(" ")
                .append("hrs"));

        holder.ivRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRating;
        TextView tvTime, tvStatus, tvAmount, tvPickupCity, tvDeliverCity, tvRef, tvRefNo, tvPickFrom, tvDeliverFrom;
        public ViewHolder(View itemView) {
            super(itemView);

            ivRating=(ImageView)itemView.findViewById(R.id.ivRating);
            tvTime=(TextView)itemView.findViewById(R.id.tvTime);
            tvStatus =(TextView)itemView.findViewById(R.id.tvStatus);
            tvAmount =(TextView)itemView.findViewById(R.id.tvAmount);
            tvPickupCity=(TextView)itemView.findViewById(R.id.tvPickupCity);
            tvDeliverCity =(TextView)itemView.findViewById(R.id.tvDeliverCity);
            tvRef=(TextView)itemView.findViewById(R.id.tvRef);
            tvRefNo =(TextView)itemView.findViewById(R.id.tvRefNo);
            tvPickFrom=(TextView)itemView.findViewById(R.id.tvPickFrom);
            tvDeliverFrom=(TextView)itemView.findViewById(R.id.tvDeliverFrom);

            tvTime.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvRefNo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvStatus.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_BOLD));
            tvAmount.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_BOLD));
            tvRef.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_BOLD));
            tvPickFrom.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
            tvPickupCity.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
            tvDeliverFrom.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
            tvDeliverCity.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        }
    }

}

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
import com.pikndel.model.OrderList;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.TimeStampFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sujeet on 03-05-2016.
 */
public class CompleteFragmentAdapter  extends RecyclerView.Adapter<CompleteFragmentAdapter.ViewHolder> {
    private Context context;
    private List<OrderList> orderList =new ArrayList<>();
    private List<OrderList> orderListFilter =new ArrayList<>();
    private RecyclerItemClickListener listener;

    public CompleteFragmentAdapter(Context context, List<OrderList> orderList, RecyclerItemClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        orderListFilter.addAll(orderList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_complete_fragment, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        OrderList orderListModel = orderList.get(position);

        if (!TextUtils.isEmpty(orderListModel.pickUpLocation) && !TextUtils.isEmpty(orderListModel.deliveryLocation) ){
            holder.tvPickupCity.setText(TextUtils.isEmpty(orderListModel.pickUpLocation)?"":orderListModel.pickUpLocation);
            holder.tvDeliverCity.setText(TextUtils.isEmpty(orderListModel.deliveryLocation)?"":orderListModel.deliveryLocation);
        }else {
            holder.tvPickupCity.setText(TextUtils.isEmpty(orderListModel.fromCity)?"":orderListModel.fromCity);
            holder.tvDeliverCity.setText(TextUtils.isEmpty(orderListModel.toCity)?"":orderListModel.toCity);
        }

        /*holder.tvRefNo.setText(TextUtils.isEmpty(orderListModel.referenceNumber)?"":orderListModel.referenceNumber);
        holder.tvAmount.setText("₹ "+String.format(Locale.US, "%.2f", orderListModel.finalAmount));
        holder.tvTime.setText(new StringBuilder()
                .append(TextUtils.isEmpty(orderListModel.deliveryByDate)?"": TimeStampFormatter.getValueFromTS(orderListModel.deliveryByDate, "MMM dd yyyy"))
                .append(" ")
                .append(TextUtils.isEmpty(orderListModel.deliveryByTime)?"": TimeStampFormatter.changeDateTimeFormat(orderListModel.deliveryByTime, "HH:mm:ss", "HH:mm")));*/

        holder.ivInfo.setOnClickListener(new View.OnClickListener() {
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
        ImageView ivInfo;
        TextView tvPickupCity, tvDeliverCity, tvPickFrom , tvDeliverTo;
        ;
        public ViewHolder(View itemView) {
            super(itemView);
            ivInfo=(ImageView)itemView.findViewById(R.id.ivInfo);
            tvPickupCity=(TextView)itemView.findViewById(R.id.tvPickupCity);
            tvDeliverCity =(TextView)itemView.findViewById(R.id.tvDeliverCity);
            tvPickFrom=(TextView)itemView.findViewById(R.id.tvPickFrom);
            tvDeliverTo=(TextView)itemView.findViewById(R.id.tvDeliverTo);

            tvPickFrom.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
            tvPickupCity.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
            tvDeliverTo.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
            tvDeliverCity.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_MEDIUM));
        }
    }

    public void applyFilters(List<String> months, String year){
        orderList.clear();
        if (months.size()>0 && !TextUtils.isEmpty(year)) {
            for (int i = 0; i < orderListFilter.size(); i++) {
                if (!TextUtils.isEmpty(orderListFilter.get(i).deliveryByTime)) {
                    for (int j = 0; j < months.size(); j++) {
                        String[] start = TimeStampFormatter.getValueFromTS(orderListFilter.get(i).deliveryByDate, "MMM-yyyy").split("-");
                        if (start[1].equalsIgnoreCase(year)) {
                            if (start[0].equalsIgnoreCase(months.get(j))) {
                                orderList.add(orderListFilter.get(i));
                            }
                        }
                    }
                }
            }
        }else {
            orderList.addAll(orderListFilter);
        }

        notifyDataSetChanged();
    }

}

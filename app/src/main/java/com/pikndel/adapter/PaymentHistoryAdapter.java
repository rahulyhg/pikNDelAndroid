package com.pikndel.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pikndel.R;
import com.pikndel.services.PaymentHistoryList;
import com.pikndel.utils.TextFonts;
import com.pikndel.utils.TimeStampFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by govind_gautam on 10/5/16.
 */
public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>{

    private Context context;
    private List<PaymentHistoryList> list;

    public PaymentHistoryAdapter(Context context, List<PaymentHistoryList> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(context).inflate(R.layout.row_payment_history,parent,false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PaymentHistoryList model = list.get(position);


    /*    Calendar calendar = Calendar.getInstance();


        try {
            long l = Long.parseLong(model.transactionDate);

            calendar.setTimeInMillis(l);
            SimpleDateFormat month_date = new SimpleDateFormat("MMM dd yyyy");

          holder.tvRefNumber.setText(month_date.format(calendar.getTime()));

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
*/





//        holder.tvRefNumber.setText(model.transactionDate);
        holder.tvRefNumber.setText(TextUtils.isEmpty(model.transactionDate)?"": TimeStampFormatter.getValueFromTS(model.transactionDate, "dd-MMM-yyyy"));

        holder.tvBal.setText(model.sign + "₹ "+model.amount);

//        holder.tvTran.setText(TextUtils.isEmpty(model.transactionId)?model.remark: model.transactionId);

        if(model.transactionId.equals("")){

            holder.tvTran.setText(model.remark);
            holder.tvTranNumber.setText("Remark");

        }else {

            holder.tvTran.setText(model.transactionId);
            holder.tvTranNumber.setText("Transaction Id");

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBal,tvRefNumber;
        TextView tvTran,tvTranNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBal = (TextView)itemView.findViewById(R.id.tvbal);
            tvRefNumber = (TextView)itemView.findViewById(R.id.tvRefNumber);
            tvRefNumber.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvBal.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvTran = (TextView)itemView.findViewById(R.id.tvTran);
            tvTranNumber = (TextView)itemView.findViewById(R.id.tvTranNumber);
            tvTranNumber.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
            tvTran.setTypeface(TextFonts.setFontFamily(context, TextFonts.RALEWAY_REGULAR));
        }
    }
}

package org.ttweb.halkhyzmat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ttweb.halkhyzmat.R;
import org.ttweb.halkhyzmat.model.Charge;
import org.ttweb.halkhyzmat.model.Payment;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private Payment payment;
    private List<Charge> mData;
    private List<Charge> chargeList;

    private final OnItemClickListener listener;

    public RecyclerViewAdapter(Context mContext, Payment payment, OnItemClickListener listener) {

        Charge charge = new Charge(
                payment.getPaymentSystemId(),
                0,
                "Töleg Jemi",
                "",
                payment.getAmount(),
                "TMT"
        );

        chargeList = payment.getChargeList() != null ? payment.getChargeList() : new ArrayList<Charge>();

        chargeList.add(charge);

        this.mContext = mContext;
        this.payment = payment;
        this.mData = chargeList;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.payment_row_item, parent, false);

        // click listener here
        final MyViewHolder viewHolder = new MyViewHolder(view) ;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //animate payment row here
        holder.viewContainer.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation));

        //set data of animate row here
        String payText = mData.get(position).getDebt() + " " + mData.get(position).getCurrencyTitle();
        holder.serviceName.setText(String.valueOf(mData.get(position).getServiceName()));
        holder.paymentAmount.setText(payText);

        holder.bind(mData.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView serviceName;
        TextView paymentAmount;
        RelativeLayout viewContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            viewContainer = itemView.findViewById(R.id.paymentRow);
            serviceName = itemView.findViewById(R.id.service);
            paymentAmount = itemView.findViewById(R.id.paymentAmount);
        }

        public void bind(final Charge charge, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(charge);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Charge charge);
    }
}

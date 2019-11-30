package org.ttweb.halkhyzmat.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ttweb.halkhyzmat.MainActivity;
import org.ttweb.halkhyzmat.R;
import org.ttweb.halkhyzmat.WebViewActivity;
import org.ttweb.halkhyzmat.model.Charge;
import org.ttweb.halkhyzmat.model.Order;
import org.ttweb.halkhyzmat.model.Payment;
import org.ttweb.halkhyzmat.service.PaymentClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    //RequestOptions options;
    private Context mContext;
    private Payment payment;
    private List<Charge> mData;

    //Retrofit
    private PaymentClient paymentClient;

    public RecyclerViewAdapter(Context mContext, Payment payment) {


        this.mContext = mContext;
        this.payment = payment;
        this.mData = payment.getChargeList();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        paymentClient = retrofit.create(PaymentClient.class);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.payment_row_item, parent, false);

        // click listener here
        final MyViewHolder viewHolder = new MyViewHolder(view) ;
        viewHolder.viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> postData = new HashMap<>();

                SharedPreferences sharedPref = mContext.getSharedPreferences(MainActivity.USER_DATA, MODE_PRIVATE);
                final String sessionId = sharedPref.getString("SESSION_ID", "");
                final String login = sharedPref.getString("LOGIN", "");
                final String bxUserId = sharedPref.getString("BX_USER_ID", "");
                final String bxUidh = sharedPref.getString("BX_UIDH", "");
                final String sessid = sharedPref.getString("SESSID", "");

                postData.put("pay_system_id", String.valueOf(payment.getPaymentSystem().get("ID")));
                postData.put("meters_id", mData.get(viewHolder.getAdapterPosition()).getMeterId());
                postData.put("pay_amount", String.valueOf(mData.get(viewHolder.getAdapterPosition()).getDebt()));
                postData.put("sessid", sessid);

                String cookie = "PHPSESSID=" + sessionId + "; BX_USER_ID=" + bxUserId + "; " +
                        "BITRIX_SM_LOGIN=" + login + "; BITRIX_SM_SOUND_LOGIN_PLAYED=Y;" +
                        " BITRIX_SM_UIDH=" + bxUidh + "; BITRIX_SM_UIDL=" + login + ";";

                createPayment(cookie, postData);
            }
        });

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
    }

    private void createPayment(String cookie, Map<String, String> postData){

        final Call<Order> createPaymentCall = paymentClient.createPayment(
                cookie,
                postData.get("pay_system_id"),
                postData.get("meters_id"),
                postData.get("pay_amount"),
                postData.get("sessid"));

        createPaymentCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.isSuccessful()) {

                    //Start new Activity here and pass paymentId
                    Intent i = new Intent(mContext, WebViewActivity.class);
                    i.putExtra("payment", response.body().getPaymentId());
                    mContext.startActivity(i);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Log.e("CreatePayment", "fail: " + t.getMessage());
                Toast.makeText(mContext,"Ýalňyşlyk: Serwere baglanyp bolmady!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package org.ttweb.halkhyzmat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.ttweb.halkhyzmat.adapter.RecyclerViewAdapter;
import org.ttweb.halkhyzmat.model.Order;
import org.ttweb.halkhyzmat.model.Payment;
import org.ttweb.halkhyzmat.service.PaymentClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {
    private String cookie;

    private RecyclerView myRv ;
    private RecyclerViewAdapter rvAdapter;

    private ProgressBar progressBar;

    private TextView paymentTotal;
    private String sessid;

    //Retrofit
    private PaymentClient paymentClient;

    private Payment payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

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

        myRv = findViewById(R.id.rv);

        progressBar = findViewById(R.id.paymentProgressBar);

        paymentTotal = findViewById(R.id.paymentTotal);

        SharedPreferences sharedPref = getSharedPreferences(MainActivity.USER_DATA, MODE_PRIVATE);
        String sessionId = sharedPref.getString("SESSION_ID", "");
        String login = sharedPref.getString("LOGIN", "");
        String bxUserId = sharedPref.getString("BX_USER_ID", "");
        String bxUidh = sharedPref.getString("BX_UIDH", "");

        sessid = sharedPref.getString("SESSID", "");

        String toolbarTitle;
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.paymentCollapsingToolBarLayout);

        toolbarTitle = sharedPref.getString("USER_NAME", "");

        toolbarLayout.setTitle(toolbarTitle);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        Toolbar mToolbar = findViewById(R.id.paymentToolbarId);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_white_black_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cookie = "PHPSESSID=" + sessionId + "; BX_USER_ID=" + bxUserId + "; " +
                "BITRIX_SM_LOGIN=" + login + "; BITRIX_SM_SOUND_LOGIN_PLAYED=Y;" +
                " BITRIX_SM_UIDH=" + bxUidh + "; BITRIX_SM_UIDL=" + login + ";";

        getPaymentsList();
    }

    public void payTotal(View view){
        if(Float.valueOf(paymentTotal.getText().toString().substring(0, paymentTotal.getText().toString().indexOf(" "))) > 0.00){
            Map<String, String> postData = new HashMap<>();

            postData.put("pay_system_id", String.valueOf(payment.getPaymentSystem().get("ID")));
            postData.put("meters_id", "");
            postData.put("pay_amount", String.valueOf(payment.getAmount()));
            postData.put("sessid", sessid);

            Log.i("TOTAL PAYMENT", "total amount: " + payment.getAmount());
            Log.i("TOTAL PAYMENT", "sessid: " + sessid);

            createPayment(cookie, postData);

        }else{
            Toast.makeText(PaymentActivity.this,"Tölemek üçin töleg jemi ýetersiz!", Toast.LENGTH_SHORT).show();
        }
    }

    //Create payment for pay
    private void createPayment(String cookie, Map<String, String> postData){
        progressBar.setVisibility(ProgressBar.VISIBLE);

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
                    Log.i("WEBVIEW", "pmt ID: " + response.body().getPaymentId());
                    progressBar.setVisibility(ProgressBar.INVISIBLE);

                    //Start new Activity here
                    goToWebView(response.body().getPaymentId());

                }else{
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Log.e("CreatePayment", "fail: " + t.getMessage());
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                Toast.makeText(PaymentActivity.this,"Ýalňyşlyk: Serwere baglanyp bolmady!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToWebView(int paymentId){
        Intent i = new Intent(this, WebViewActivity.class);
        i.putExtra("payment", paymentId);
        startActivity(i);
    }

    //method to get list of payments
    private void getPaymentsList(){
        Call<Payment> call = paymentClient.getPayments(cookie);

        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(@NonNull Call<Payment> call, @NonNull Response<Payment> response) {
                if (response.isSuccessful()) {
                    setRvadapter(response.body());
                    Toast.makeText(PaymentActivity.this,"Jaý-jemagat tölegleri", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payment> call, @NonNull Throwable t) {
                Log.e("PAYMENT", t.getMessage());
                Toast.makeText(PaymentActivity.this,"Ýalňyşlyk: Serwere baglanyp bolmady!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setRvadapter (Payment payment) {
        rvAdapter = new RecyclerViewAdapter(this, payment);
        myRv.setLayoutManager(new LinearLayoutManager(this));
        myRv.setAdapter(rvAdapter);

        this.payment = payment;

        String total = payment.getAmount() + " TMT";
        paymentTotal.setText(total);
    }
}

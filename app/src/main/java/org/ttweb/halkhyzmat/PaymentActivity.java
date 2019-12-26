package org.ttweb.halkhyzmat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.ttweb.halkhyzmat.adapter.RecyclerViewAdapter;
import org.ttweb.halkhyzmat.model.Charge;
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

    private ProgressBar progressBar;
    private EditText paymentPrepayAmount;

    private String sessid;

    private Payment payment;

    //Retrofit
    private PaymentClient paymentClient;

    private Call<Payment> paymentCall;
    private Call<Order> createPaymentCall;

    private View paymentView;

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

        paymentView = findViewById(R.id.paymentActivityView);

        myRv = findViewById(R.id.rv);

        progressBar = findViewById(R.id.paymentProgressBar);

        paymentPrepayAmount = findViewById(R.id.paymentPrepayAmount);

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

    @Override
    public void onStop() {
        super.onStop();

        if(paymentCall != null && paymentCall.isExecuted()){
            paymentCall.cancel();
        }

        if(createPaymentCall != null && createPaymentCall.isExecuted()){
            createPaymentCall.cancel();
        }

        progressBar.setVisibility(ProgressBar.GONE);
    }

    //method to get list of payments
    private void getPaymentsList(){
        progressBar.setVisibility(ProgressBar.VISIBLE);
        paymentCall = paymentClient.getPayments(cookie);

        paymentCall.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(@NonNull Call<Payment> call, @NonNull Response<Payment> response) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    setRvadapter(response.body());
                }else {
                    Snackbar.make(paymentView, R.string.payment_empty, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Payment> call, @NonNull Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                Snackbar.make(paymentView, R.string.connection_failed, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void setRvadapter (Payment payment) {
        this.payment = payment;

        final String paymentSystemId;
        if(payment.getPaymentSystem() != null) {
            paymentSystemId = payment.getPaymentSystem().get("ID");
        } else{
            paymentSystemId = "0";
        }


        RecyclerViewAdapter rvAdapter = new RecyclerViewAdapter(this, payment, new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Charge charge) {
                if(charge.getDebt() > 0.0){
                    Map<String, String> postData = new HashMap<>();

                    postData.put("pay_system_id", paymentSystemId);
                    postData.put("meters_id", charge.getMeterId());
                    postData.put("pay_amount", String.valueOf(charge.getDebt()));
                    postData.put("sessid", sessid);

                    createPayment(cookie, postData);
                }else{
                    Snackbar.make(paymentView, R.string.payment_request_zero, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        myRv.setLayoutManager(new LinearLayoutManager(this));
        myRv.setAdapter(rvAdapter);
    }

    public void payTotal(View view){
        if(!paymentPrepayAmount.getText().toString().isEmpty() && Float.valueOf(paymentPrepayAmount.getText().toString()) > 0.0){
            Map<String, String> postData = new HashMap<>();

            postData.put("pay_system_id", String.valueOf(payment.getPaymentSystem().get("ID")));
            postData.put("meters_id", "");
            postData.put("pay_amount", paymentPrepayAmount.getText().toString());
            postData.put("sessid", sessid);

            createPayment(cookie, postData);

        }else{
            Snackbar.make(view, R.string.payment_request_zero, Snackbar.LENGTH_LONG).show();
        }
    }

    //Create payment for pay
    private void createPayment(String cookie, Map<String, String> postData){
        progressBar.setVisibility(ProgressBar.VISIBLE);

        createPaymentCall = paymentClient.createPayment(
                cookie,
                postData.get("pay_system_id"),
                postData.get("meters_id"),
                postData.get("pay_amount"),
                postData.get("sessid"));

        createPaymentCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    //Start new Activity here
                    goToWebView(response.body().getPaymentId());

                }else{
                    Snackbar.make(paymentView, response.message(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                Snackbar.make(paymentView, R.string.connection_failed, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void goToWebView(int paymentId){
        Intent i = new Intent(this, WebViewActivity.class);
        i.putExtra("payment", paymentId);
        startActivity(i);
    }
}

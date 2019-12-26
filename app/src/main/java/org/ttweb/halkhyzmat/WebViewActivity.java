package org.ttweb.halkhyzmat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.ttweb.halkhyzmat.model.BankResponse;
import org.ttweb.halkhyzmat.model.Order;
import org.ttweb.halkhyzmat.model.PaymentStatus;
import org.ttweb.halkhyzmat.service.BankClient;
import org.ttweb.halkhyzmat.service.PaymentClient;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebViewActivity extends AppCompatActivity {
    private String cookie;

    //Retrofit
    GsonConverterFactory gsonFactory;
    private PaymentClient paymentClient;

    private WebView webView;
    private Order order;

    Call<Order> paymentCall;
    Call<BankResponse> bankCall;
    Call<PaymentStatus> checkCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent i = getIntent();

        int paymentId = i.getExtras().getInt("payment");

        Log.e("Merchant", "Merchant ID: " + paymentId);

        gsonFactory = GsonConverterFactory.create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(gsonFactory);

        Retrofit retrofit = builder.build();

        paymentClient = retrofit.create(PaymentClient.class);

        SharedPreferences sharedPref = getSharedPreferences(MainActivity.USER_DATA, MODE_PRIVATE);
        String sessionId = sharedPref.getString("SESSION_ID", "");
        String login = sharedPref.getString("LOGIN", "");
        String bxUserId = sharedPref.getString("BX_USER_ID", "");
        String bxUidh = sharedPref.getString("BX_UIDH", "");

        cookie = "PHPSESSID=" + sessionId + "; BX_USER_ID=" + bxUserId + "; " +
                "BITRIX_SM_LOGIN=" + login + "; BITRIX_SM_SOUND_LOGIN_PLAYED=Y;" +
                " BITRIX_SM_UIDH=" + bxUidh + "; BITRIX_SM_UIDL=" + login + ";";

        getOrder(paymentId);

    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(paymentCall != null && paymentCall.isExecuted()){
            //isAlert = false;
            paymentCall.cancel();
        }

        if(bankCall != null && bankCall.isExecuted()){
            //isAlert = false;
            bankCall.cancel();
        }

        if(checkCall != null && checkCall.isExecuted()){
            //isAlert = false;
            checkCall.cancel();
        }
    }


    private void getOrder(int paymentId){
        paymentCall = paymentClient.getPaymentData(cookie, paymentId);

        paymentCall.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i("ORDER", "Payment ID: " + response.body().getPaymentId());
                    registerOrder(response.body());
                }else {
                    Log.e("ORDER", "Failed Payment ID:::::: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Log.e("ORDER", "Fail: " + t.getMessage());
            }
        });
    }

    private void registerOrder(Order order){
        this.order = order;

        int amount = (int) (order.getPaymentAmount() * 100);
        String sign = "";//sha1("$orderId:$amount:$description:$description:$orderId:$amount");
        String value = order.getPaymentId() + ":" + amount + ":" + order.getDescription() +
                ":" + order.getDescription() + ":" + order.getPaymentId() + ":" + amount;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(value.getBytes("utf8"));
            sign = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e){
            e.printStackTrace();
        }

        String failUrl = order.getMerchantFailUrl() + "?ORDER_ID=" + order.getPaymentId();

        String returnUrl = order.getMerchantSuccessUrl() + "?ORDER_ID=" + order.getPaymentId() + "&sign=" + sign + "&origOrderId=" + order.getPaymentId() + "&type=mobile";
        try {
            failUrl = URLEncoder.encode(failUrl, "UTF-8");
            returnUrl = URLEncoder.encode(returnUrl,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Retrofit.Builder bBuilder = new Retrofit.Builder()
                .baseUrl(order.getMerchantBaseUrl())
                .addConverterFactory(gsonFactory);

        Retrofit retrofit = bBuilder.build();

        BankClient bankClient = retrofit.create(BankClient.class);

        bankCall = bankClient.registerOrder(
                order.getMerchantCurrency(),
                order.getMerchantLanguage(),
                order.getMerchantPageView(),
                order.getDescription(),
                order.getPaymentId(),
                failUrl,
                order.getMerchantId(),
                order.getMerchantPassword(),
                amount,
                order.getSessionTimeoutSecs(),
                returnUrl
        );

        bankCall.enqueue(new Callback<BankResponse>() {
            @Override
            public void onResponse(@NonNull Call<BankResponse> call, @NonNull Response<BankResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getErrorCode() == 0) {
                        Log.i("BANK ORDER", "Url: " + response.body().getFormUrl());
                        openWebview(response.body().getFormUrl());
                    }else{
                        Log.d("BANK ORDER", "Error Message: " + response.body().getErrorMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankResponse> call, @NonNull Throwable t) {
                Log.e("BANK ORDER", "Fail: " + t.getMessage());
            }
        });
    }

    private void openWebview(String url){
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new MyBrowser(this, order, cookie));
    }

    private void goToPayment(){
        Intent intent = new Intent(this, PaymentActivity.class);
        startActivity(intent);
    }

    private void goToDashboard(boolean status){
        Intent intent = new Intent(this, DashboardActivity.class);
        String message;
        if(status){
            message = getResources().getString(R.string.payment_success);
        }else{
            message = getResources().getString(R.string.payment_failed);
        }

        intent.putExtra("status", true);
        intent.putExtra("payment_message", message);
        startActivity(intent);
    }

    private void isPayed(String returnUrl){
        if (returnUrl.contains("sign")) {
            Map<String, String> params;

            try {
                params = getQueryParams(returnUrl);

                Retrofit.Builder bBuilder = new Retrofit.Builder()
                        .baseUrl(MainActivity.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = bBuilder.build();

                PaymentClient bankClient = retrofit.create(PaymentClient.class);

                checkCall = bankClient.isPayed(
                        cookie,
                        params.get("origOrderId"),
                        params.get("sign"),
                        params.get("origOrderId"),
                        params.get("type"),
                        params.get("orderId")
                );

                checkCall.enqueue(new Callback<PaymentStatus>() {
                    @Override
                    public void onResponse(@NonNull Call<PaymentStatus> call, @NonNull Response<PaymentStatus> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.i("PAYMENT STATUS", "Status: " + response.body().getStatus());
                            goToDashboard(response.body().getStatus());
                        }else{
                            goToDashboard(false);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PaymentStatus> call, @NonNull Throwable t) {
                        Log.e("PAYMENT STATUS", "Failed: " + t.getMessage());
                        goToDashboard(false);
                    }
                });

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, String> getQueryParams(String url) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String urlDecoded = URLDecoder.decode(url, "UTF-8");

        String[] urlParts = urlDecoded.split("\\?");
        if (urlParts.length < 2) {
            return params;
        }

        String query = urlParts[1];
        if(urlParts.length == 3){
            query += "&" + urlParts[2];
        }

        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            String key = URLDecoder.decode(pair[0], "UTF-8");
            String value = "";
            if (pair.length > 1) {
                value = URLDecoder.decode(pair[1], "UTF-8");
            }

            // skip ?& and &&
            if ("".equals(key) && pair.length == 1) {
                continue;
            }

            params.put(key, value);
        }

        return params;
    }

    private class MyBrowser extends WebViewClient {
        Context context;
        Order order;
        String cookie;

        private MyBrowser(Context context, Order order, String cookie){
            this.context = context;
            this.order = order;
            this.cookie = cookie;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(!url.contains("ORDER_ID")){
                Log.i("URL", url);
                view.loadUrl(url);
            }else{
                Log.i("URL ORDER_ID", url);
                isPayed(url);
                //view.loadUrl(MainActivity.BASE_URL);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}

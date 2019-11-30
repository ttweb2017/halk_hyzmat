package org.ttweb.halkhyzmat.model;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("ALTYN_ASYR_MERCHANT_ID")
    private String merchantId;

    @SerializedName("ALTYN_ASYR_MERCHANT_PASSWORD")
    private String merchantPassword;

    @SerializedName("ALTYN_ASYR_CURRENCY_ID")
    private String merchantCurrency;

    @SerializedName("ALTYN_ASYR_LANGUAGE")
    private String merchantLanguage;

    @SerializedName("ALTYN_ASYR_PAGE_VIEW")
    private String merchantPageView;

    @SerializedName("ALTYN_ASYR_BASE_URL")
    private String merchantBaseUrl;

    @SerializedName("ALTYN_ASYR_SUCCESS_URL")
    private String merchantSuccessUrl;

    @SerializedName("ALTYN_ASYR_FAIL_URL")
    private String merchantFailUrl;

    @SerializedName("PAYMENT_ID")
    private int paymentId;

    @SerializedName("PAYMENT_AMOUNT")
    private float paymentAmount;

    private final String description = "Toleg";

    private final int sessionTimeoutSecs = 300;

    public Order(String merchantId, String merchantPassword, String merchantCurrency,
                 String merchantLanguage, String merchantPageView, String merchantBaseUrl,
                 String merchantSuccessUrl, String merchantFailUrl, int paymentId,
                 float paymentAmount) {
        this.merchantId = merchantId;
        this.merchantPassword = merchantPassword;
        this.merchantCurrency = merchantCurrency;
        this.merchantLanguage = merchantLanguage;
        this.merchantPageView = merchantPageView;
        this.merchantBaseUrl = merchantBaseUrl;
        this.merchantSuccessUrl = merchantSuccessUrl;
        this.merchantFailUrl = merchantFailUrl;
        this.paymentId = paymentId;
        this.paymentAmount = paymentAmount;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantPassword() {
        return merchantPassword;
    }

    public void setMerchantPassword(String merchantPassword) {
        this.merchantPassword = merchantPassword;
    }

    public String getMerchantCurrency() {
        return merchantCurrency;
    }

    public void setMerchantCurrency(String merchantCurrency) {
        this.merchantCurrency = merchantCurrency;
    }

    public String getMerchantLanguage() {
        return merchantLanguage;
    }

    public void setMerchantLanguage(String merchantLanguage) {
        this.merchantLanguage = merchantLanguage;
    }

    public String getMerchantPageView() {
        return merchantPageView;
    }

    public void setMerchantPageView(String merchantPageView) {
        this.merchantPageView = merchantPageView;
    }

    public String getMerchantBaseUrl() {
        return merchantBaseUrl;
    }

    public void setMerchantBaseUrl(String merchantBaseUrl) {
        this.merchantBaseUrl = merchantBaseUrl;
    }

    public String getMerchantSuccessUrl() {
        return merchantSuccessUrl;
    }

    public void setMerchantSuccessUrl(String merchantSuccessUrl) {
        this.merchantSuccessUrl = merchantSuccessUrl;
    }

    public String getMerchantFailUrl() {
        return merchantFailUrl;
    }

    public void setMerchantFailUrl(String merchantFailUrl) {
        this.merchantFailUrl = merchantFailUrl;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public float getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getDescription() {
        return description;
    }

    public int getSessionTimeoutSecs() {
        return sessionTimeoutSecs;
    }
}

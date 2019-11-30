package org.ttweb.halkhyzmat.model;

import com.google.gson.annotations.SerializedName;

public class Charge {
    @SerializedName("PERIOD_ID")
    private int periodId;

    @SerializedName("SERVICE_ID")
    private int serviceId;

    @SerializedName("SERVICE_NAME")
    private String serviceName;

    @SerializedName("METER_XML_ID")
    private String meterId;

    @SerializedName("DEBT_END")
    private float debt;

    @SerializedName("CURRENCY_TITLE")
    private String currencyTitle;

    public Charge(int periodId, int serviceId, String serviceName, String meterId, float debt, String currencyTitle) {
        this.periodId = periodId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.meterId = meterId;
        this.debt = debt;
        this.currencyTitle = currencyTitle;
    }

    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public float getDebt() {
        return debt;
    }

    public void setDebt(float debt) {
        this.debt = debt;
    }

    public String getCurrencyTitle() {
        return currencyTitle;
    }

    public void setCurrencyTitle(String currencyTitle) {
        this.currencyTitle = currencyTitle;
    }
}

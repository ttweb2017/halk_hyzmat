package org.ttweb.halkhyzmat.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class Payment {
    @SerializedName("PAY_SYSTEM")
    private Map<String, String> paymentSystem;

    @SerializedName("ID")
    private int paymentSystemId;

    @SerializedName("METER_ID")
    private String meterId;

    @SerializedName("AMOUNT_TO_SHOW")
    private float amount;

    @SerializedName("LAST_PERIOD")
    private String lastPeriod;

    @SerializedName("XML_ID")
    private String accountNo;

    //@SerializedName("TKM_PAYMENT_CHARGES")
    private List chargeArray;

    @SerializedName("TKM_PAYMENT_CHARGES")
    private List<Charge> chargeList;

    public Payment(){

    }

    public Payment(Map<String, String> paymentSystem, int paymentSystemId, String meterId,
                   float amount, String lastPeriod, String accountNo, List<Charge> chargeList) {
        this.paymentSystem = paymentSystem;
        this.paymentSystemId = paymentSystemId;
        this.meterId = meterId;
        this.amount = amount;
        this.lastPeriod = lastPeriod;
        this.accountNo = accountNo;
        this.chargeList = chargeList;
    }

    public int getPaymentSystemId() {
        return paymentSystemId;
    }

    public void setPaymentSystemId(int paymentSystemId) {
        this.paymentSystemId = paymentSystemId;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getLastPeriod() {
        return lastPeriod;
    }

    public void setLastPeriod(String lastPeriod) {
        this.lastPeriod = lastPeriod;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Map<String, String> getPaymentSystem() {
        return paymentSystem;
    }

    public void setPaymentSystem(Map<String, String> paymentSystem) {
        this.paymentSystem = paymentSystem;
    }

    public List<Charge> getChargeList() {
        return chargeList;
    }

    public void setChargeList(List<Charge> chargeList) {
        this.chargeList = chargeList;
    }

    public List getChargeArray() {
        return chargeArray;
    }

    public void setChargeArray(List chargeArray) {
        this.chargeArray = chargeArray;
    }
}

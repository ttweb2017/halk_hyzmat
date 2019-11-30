package org.ttweb.halkhyzmat.model;

import com.google.gson.annotations.SerializedName;

public class PaymentStatus {
    @SerializedName("PAYED")
    private boolean status;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

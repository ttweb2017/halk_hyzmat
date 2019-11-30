package org.ttweb.halkhyzmat.service;


import org.ttweb.halkhyzmat.model.BankResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BankClient {

    @GET("/payment/rest/register.do")
    Call<BankResponse> registerOrder(
            @Query("currency") String currency,
            @Query("language") String language,
            @Query("pageView") String pageView,
            @Query("description") String description,
            @Query("orderNumber") int paymentId,
            @Query("failUrl") String failUrl,
            @Query("userName") String username,
            @Query("password") String password,
            @Query("amount") int amount,
            @Query("sessionTimeoutSecs") int sec,
            @Query("returnUrl") String returnUrl);
}

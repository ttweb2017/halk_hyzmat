package org.ttweb.halkhyzmat.service;


import org.ttweb.halkhyzmat.model.Order;
import org.ttweb.halkhyzmat.model.Payment;
import org.ttweb.halkhyzmat.model.PaymentStatus;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentClient {
    @GET("/mobile/tszh_mobile_payment.php")
    Call<Payment> getPayments(@Header("Cookie") String cookie);

    @FormUrlEncoded
    @POST("/mobile/tszh_mobile_payment.php")
    Call<Order> createPayment(
            @Header("Cookie") String cookie,
            @Field("pay_system_id") String paymentId,
            @Field("meters_id") String metersId,
            @Field("pay_amount") String payAmount,
            @Field("sessid") String sessionId);

    @GET("/mobile/tszh_mobile_payment.php")
    Call<Order> getPaymentData(@Header("Cookie") String cookie, @Query("payment") int paymentId);

    @GET("/personal/payment/altynasyr_result.php")
    Call<PaymentStatus> isPayed(
            @Header("Cookie") String cookie,
            @Query("ORDER_ID") String paymentId,
            @Query("sign") String sign,
            @Query("origOrderId") String paymentNo,
            @Query("type") String type,
            @Query("orderId") String bankOrderId);
}

package org.ttweb.halkhyzmat.service;

import org.ttweb.halkhyzmat.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserClient {
    @FormUrlEncoded
    @POST("/bitrix/admin/mobile/tszh_mobile_login.php")
    Call<User> login(@Field("LOGIN") String login, @Field("PASSWORD") String password);
}

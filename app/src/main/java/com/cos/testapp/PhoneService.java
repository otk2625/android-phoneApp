package com.cos.testapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PhoneService {

    @GET("phone")
    Call<CMRespDto<List<Phone>>> findAll();

    @PUT("phone/{id}")
    Call<CMRespDto<Phone>> update(@Path("id") Long id, @Body Phone phone);

    @POST("phone")
    Call<CMRespDto<Phone>> save(@Body Phone phone);

    @DELETE("phone/{id}")
    Call<CMRespDto<String>> delete(@Path("id") Long id);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.25.18:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

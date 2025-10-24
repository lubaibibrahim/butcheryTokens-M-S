package com.ms.butcharytokens.retrofit;

import com.ms.butcharytokens.model.NewTokenRequest;
import com.ms.butcharytokens.model.NewTokenResponse;
import com.ms.butcharytokens.model.TokenStatusUpdateResponse;
import com.ms.butcharytokens.model.TokenlistResponseItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @POST("create/")
    Call<NewTokenResponse> GenerateToken(@Body NewTokenRequest request);

    @POST("update/{token}/")
    Call<TokenStatusUpdateResponse> UpdateStatus(@Path("token") String token);

    @GET("list/")
    Call<ArrayList<TokenlistResponseItem>> Tokenlist(@Query("store") String store,@Query("dept") String dept);

   }

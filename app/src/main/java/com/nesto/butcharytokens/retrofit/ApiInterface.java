package com.nesto.butcharytokens.retrofit;

import com.nesto.butcharytokens.model.NewTokenRequest;
import com.nesto.butcharytokens.model.NewTokenResponse;
import com.nesto.butcharytokens.model.TokenlistResponse;
import com.nesto.butcharytokens.model.TokenlistResponseItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiInterface {

    @POST("create/")
    Call<NewTokenResponse> GenerateToken(@Body NewTokenRequest request);

    @GET("list/")
    Call<ArrayList<TokenlistResponseItem>> Tokenlist(@Query("store") String store);

   }

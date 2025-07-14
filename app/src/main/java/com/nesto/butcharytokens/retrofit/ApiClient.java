package com.nesto.butcharytokens.retrofit;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    public static String BASE_URL = "https://butchapi.nestoonline.com/token/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        try {
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(600, TimeUnit.SECONDS)
                    .connectTimeout(600, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } catch (Exception ignored) {
        }

        return retrofit;
    }
}

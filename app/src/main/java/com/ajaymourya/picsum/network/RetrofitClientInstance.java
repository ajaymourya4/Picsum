package com.ajaymourya.picsum.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ajay Mourya on 14,September,2019
 */

// To issue network requests to a REST API with Retrofit, we need to create an
// instance using the Retrofit.Builder class and configure it with a base URL.
public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://picsum.photos";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

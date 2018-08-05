package com.sofialopes.android.bakingapp.data.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sofia on 4/14/2018.
 */

public class RetrofitClient {

    private static Retrofit sRetrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sRetrofit;
    }
}

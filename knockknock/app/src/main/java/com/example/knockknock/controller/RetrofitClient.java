package com.example.knockknock.controller;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000")  // 서버 URL 설정
                    .addConverterFactory(GsonConverterFactory.create())  // JSON 변환을 위한 Gson 사용
                    .build();
        }
        return retrofit;
    }
}

// "http://10.0.2.2:3000"  애뮬레이터용 localhost
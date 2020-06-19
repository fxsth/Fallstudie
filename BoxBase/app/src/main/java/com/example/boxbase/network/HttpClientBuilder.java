package com.example.boxbase.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpClientBuilder {
    public static OkHttpClient getHttpClient(String token) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                    builder.header("Authorization", "Bearer " + token);
                    return chain.proceed(builder.build());
                })
                .build();
    }
}

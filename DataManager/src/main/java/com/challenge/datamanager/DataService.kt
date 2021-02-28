package com.challenge.datamanager

import com.challenge.datamanager.API.Config
import com.challenge.datamanager.API.MediaApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

 class DataService {

    fun getMediaService() : MediaApi{

        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client : OkHttpClient = OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()

        val builder : Retrofit = Retrofit.Builder().baseUrl(Config.BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return builder.create(MediaApi::class.java)
    }
}
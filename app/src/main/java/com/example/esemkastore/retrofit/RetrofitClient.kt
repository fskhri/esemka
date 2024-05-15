package com.example.esemkastore.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
  private const val BASE_URL = "http://10.0.2.2:5000/"
  val retrofitInstance: Retrofit by lazy {
    val logging = HttpLoggingInterceptor()
    logging.level = (HttpLoggingInterceptor.Level.BODY)
    val client = OkHttpClient.Builder()
    client.addInterceptor(logging)
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(client.build())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}
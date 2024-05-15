package com.example.esemkastore.service

import com.example.esemkastore.model.ServiceResponse
import retrofit2.Call
import retrofit2.http.GET

interface ServiceApi {
  @GET("api/Checkout/Service")
  fun getService() : Call<ArrayList<ServiceResponse>>
}
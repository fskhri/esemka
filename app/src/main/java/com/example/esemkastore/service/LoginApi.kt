package com.example.esemkastore.service

import com.example.esemkastore.model.LoginRequest
import com.example.esemkastore.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
  @POST("api/Login")
  fun login(
    @Body req: LoginRequest
  ) : Call<LoginResponse>

}
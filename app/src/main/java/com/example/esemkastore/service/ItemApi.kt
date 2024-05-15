package com.example.esemkastore.service

import com.example.esemkastore.model.ItemResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemApi {
  @GET("api/Home/Item")
  fun getItem() : Call<ArrayList<ItemResponse>>

  @GET("Item/Photo/{id}")
  fun getImages(
    @Path("id") id: Int
  ) : Response<ResponseBody>
}
package com.example.esemkastore.model

import java.io.Serializable

data class CartResponse (
  val id: Int,
  val name: String?,
  val count: String?,
  val price: String?
) : Serializable
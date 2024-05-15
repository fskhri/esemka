package com.example.esemkastore.model

import java.io.Serializable

data class ItemResponse (
  val id : Int,
  val name: String?,
  val description: String?,
  val price: Double,
  val stock: Int,
) : Serializable
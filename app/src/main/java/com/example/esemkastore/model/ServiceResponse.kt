package com.example.esemkastore.model

data class ServiceResponse (
  val id: Int,
  val name: String?,
  val duration: Int,
  val price: Int,
  val transaction: List<Any>
)
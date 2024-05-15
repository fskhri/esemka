package com.example.esemkastore.model

import java.io.Serializable

data class LoginResponse (
  val id: Int,
  val email: String?,
  val name: String?,
  val birthday: String?,
  val phoneNumber: String?
) : Serializable
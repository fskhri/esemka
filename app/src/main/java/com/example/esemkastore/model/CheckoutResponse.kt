package com.example.esemkastore.model

data class CheckoutResponse(
    val acceptanceDate: String,
    val detail: List<Detail>,
    val orderDate: String,
    val serviceId: Int,
    val totalPrice: Int,
    val userId: Int
)
{
    data class Detail(
        val count: Int,
        val itemId: Int
    )
}
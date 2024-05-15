package com.example.esemkastore

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkastore.adapter.CartAdapter
import com.example.esemkastore.model.CartResponse
import com.example.esemkastore.model.CheckoutResponse
import com.example.esemkastore.model.ServiceResponse
import com.example.esemkastore.retrofit.RetrofitClient
import com.example.esemkastore.service.ServiceApi
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.time.LocalDate

class CartActivity : AppCompatActivity() {
  private lateinit var tabLayout: TabLayout
  private lateinit var cartAdapter: CartAdapter
  private lateinit var rvCart : RecyclerView
  private lateinit var serviceSpinner: Spinner
  private lateinit var tvTotal: TextView
  private lateinit var btnCheckout: MaterialButton

  private var cartItem: ArrayList<CartResponse> = arrayListOf()
  private var checkoutItem: ArrayList<CheckoutResponse> = arrayListOf()


  private lateinit var name: String
  private lateinit var count: String
  private lateinit var price: String
  private lateinit var itemId: String
  private lateinit var sharedPreferences: SharedPreferences
  var totalPrice = 0

  private fun initComponents() {
    tabLayout = findViewById(R.id.tb_home)
    rvCart = findViewById(R.id.rv_cart)
    serviceSpinner = findViewById(R.id.spinner_cart)
    tvTotal = findViewById(R.id.tv_cart_total)
    btnCheckout = findViewById(R.id.btn_checkout)

    Log.i("item", name)
  }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_cart)
    supportActionBar!!.title = "Cart"
    supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#E67E23")))

    name = intent.getStringExtra("name") ?: ""
    count = intent.getStringExtra("count")?: ""
    price = intent.getStringExtra("price") ?: ""
    itemId = intent.getStringExtra("id") ?: ""

    sharedPreferences = getSharedPreferences("CartItems", Context.MODE_PRIVATE)
    initComponents()
    setupListener()
    setupList()
    loadCartItems()
    getCartItem()
    saveCartItems()

    val tab = tabLayout.getTabAt(1)
    val adapter = cartAdapter.itemCount

    tab?.setText("Cart(${adapter})")

    getService()
    loadTotalPrice()
  }

  override fun onPause() {
    super.onPause()
    saveCartItems()
  }


  private fun saveCartItems() {
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(cartItem)
    editor.putString("cartItems", json)
    editor.apply()
  }

  private fun loadCartItems() {
    val gson = Gson()
    val json = sharedPreferences.getString("cartItems", null)
    val type = object: TypeToken<ArrayList<CartResponse>>() {}.type
    cartItem = gson.fromJson(json, type) ?: ArrayList()
    cartAdapter.setData(cartItem)
  }

  private fun setupList() {
    cartAdapter = CartAdapter(this@CartActivity, arrayListOf())  { position ->
      // Call function to delete item from cartItem list
      deleteCartItem(position)
      cartAdapter.setData(cartItem)
    }
    rvCart.adapter = cartAdapter
  }

  fun deleteCartItem(position: Int) {
    cartItem.removeAt(position)
    cartAdapter.notifyItemRemoved(position)
    saveCartItems()
  }

  private fun setupListener() {
    val tab = tabLayout.getTabAt(1)
    tab?.select()

    tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
      override fun onTabSelected(p0: TabLayout.Tab?) {
        val position: Int = p0!!.position
        if(position == 0) {
          replaceActivity(HomeActivity())
          Toast.makeText(this@CartActivity, "Home", Toast.LENGTH_SHORT).show()
        } else if(position == 1) {
          replaceActivity(CartActivity())
          Toast.makeText(this@CartActivity, "Cart", Toast.LENGTH_SHORT).show()
        } else if (position == 2) {
          Toast.makeText(this@CartActivity, "History", Toast.LENGTH_SHORT).show()
        }
      }

      override fun onTabUnselected(p0: TabLayout.Tab?) {}

      override fun onTabReselected(p0: TabLayout.Tab?) {}

    })


    btnCheckout.setOnClickListener {
      if (cartAdapter.itemCount == 0) {
        Toast.makeText(this@CartActivity, "Cart Item kosong", Toast.LENGTH_SHORT).show()
      } else {
        val userId = this.getSharedPreferences("user_info", MODE_PRIVATE).getInt("id", 1)
        val serviceId = serviceSpinner.selectedItemPosition + 1
        if (serviceId != null) {
          Log.i("spinner", serviceId.toString())
        }

        for (i in cartAdapter.items) {
          val orderDate = LocalDate.now()
          val totalPrice = i.price
          val itemId = i.id
          val count = i.count
          Log.i("spinner", totalPrice.toString())
          Log.i("spinner", itemId.toString())
          Log.i("spinner", count.toString())
          Log.i("spinner", orderDate.toString())

          val detailList = mutableListOf<CheckoutResponse.Detail>()
          val detail = CheckoutResponse.Detail(count = count!!.toInt(), itemId = itemId)
          detailList.add(detail)
          val checkoutResponse = CheckoutResponse(
            userId = userId,
            serviceId = serviceId,
            totalPrice = totalPrice!!.toInt(),
            orderDate = orderDate.toString(),
            detail = detailList,
            acceptanceDate = orderDate.toString()
          )
          checkoutItem.add(checkoutResponse)
        }

        Log.i("checkout", checkoutItem.toString())

      }
    }
  }

  private fun getCartItem () {
    if (itemId.isNotEmpty()) {
      val cartResponse = CartResponse(
        id = itemId.toInt(),
        name = name,
        count = count,
        price = price
      )
      cartItem.add(cartResponse)
      saveCartItems()
      cartAdapter.setData(cartItem)
      Log.i("item", cartItem.toString())
    } else {
      Log.e("getCartItem", "ItemId is empty")
    }
  }

  private fun loadTotalPrice() {
    for(item in cartAdapter.items) {
      totalPrice += item.price!!.toInt()
    }
    tvTotal.text = "Total Price: Rp. " + totalPrice.toString()
  }

  fun loadSpinner(names: List<String?>) {
    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    serviceSpinner.adapter = adapter
  }

  private fun getService() {
    val apiService = RetrofitClient.retrofitInstance.create(ServiceApi::class.java)
    val call = apiService.getService()
    call.enqueue(object : Callback<ArrayList<ServiceResponse>> {
      override fun onResponse(
        p0: Call<ArrayList<ServiceResponse>>,
        response: Response<ArrayList<ServiceResponse>>
      ) {
        if(response.isSuccessful) {
          val result = response.body()
          val list = mutableListOf<String>()
          result?.forEach {service ->
            val formattedService = "${service.name} - Rp. ${service.price} (${service.duration} day(s) )"
            list.add(formattedService)
          }

          loadSpinner(list)
        }
      }

      override fun onFailure(p0: Call<ArrayList<ServiceResponse>>, p1: Throwable) {
        Log.i("service", p1.toString())
      }

    })
  }

  fun replaceActivity(activity: Activity) {
    val intent = Intent(this@CartActivity, activity::class.java)
    startActivity(intent)
  }
}
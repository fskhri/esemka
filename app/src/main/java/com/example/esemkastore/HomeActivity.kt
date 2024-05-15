package com.example.esemkastore

import android.accounts.Account
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.esemkastore.adapter.ItemAdapter
import com.example.esemkastore.model.ItemResponse
import com.example.esemkastore.model.LoginResponse
import com.example.esemkastore.retrofit.RetrofitClient
import com.example.esemkastore.service.ItemApi
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {
  private lateinit var tvWelcome: TextView
  private lateinit var tabLayout : TabLayout
  private lateinit var rvItem : RecyclerView
  private lateinit var itemAdapter: ItemAdapter
  private val users by lazy { intent.getSerializableExtra("login") as? LoginResponse }
  private lateinit var name : String
  private lateinit var id : String

  private fun initComponents() {
    tabLayout = findViewById(R.id.tb_home)
    rvItem = findViewById(R.id.rv_item)
    tvWelcome = findViewById(R.id.tv_welcome)
    name =  this.getSharedPreferences("user_info", MODE_PRIVATE).getString("name", null).toString()
    id =  this.getSharedPreferences("user_info", MODE_PRIVATE).getInt("id", 1).toString()
    Log.i("homeactivity", name)
    if(name != null ) {
      tvWelcome.text = "Welcome " + name + id
    } else {
      tvWelcome.text = "Welcome User"
    }
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    supportActionBar!!.title = "Home"
    supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#E67E23")))
//    val intent = Intent(this@HomeActivity, CartActivity::class.java)
//    startActivity(intent)
    initComponents()
    setupListener()
    setupList()
    getItem()
  }

  private fun setupList() {
    itemAdapter = ItemAdapter(this@HomeActivity, arrayListOf(), object: ItemAdapter.OnAdapterListener {
      override fun onDetail(result: ItemResponse) {
        val intent = Intent(this@HomeActivity, DetailActivity::class.java)
        intent.putExtra("item_data", result)
        startActivity(intent)
      }

    })
    rvItem.layoutManager = GridLayoutManager(this, 2)
    rvItem.setHasFixedSize(true)
    rvItem.adapter = itemAdapter
  }

  private fun setupListener() {
    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
      override fun onTabSelected(p0: TabLayout.Tab?) {
        val position : Int = p0!!.position
        if(position == 0) {
          replaceActivity(HomeActivity())
          Toast.makeText(this@HomeActivity, "Home", Toast.LENGTH_SHORT).show();
        } else if(position == 1) {
          replaceActivity(CartActivity())

          Toast.makeText(this@HomeActivity, "Cart", Toast.LENGTH_SHORT).show();
        } else if(position == 2) {
          Toast.makeText(this@HomeActivity, "History", Toast.LENGTH_SHORT).show();
        }
      }

      override fun onTabUnselected(p0: TabLayout.Tab?) {}

      override fun onTabReselected(p0: TabLayout.Tab?) {}

    })
  }


  private fun getItem() {
    val apiService = RetrofitClient.retrofitInstance.create(ItemApi::class.java)
    val call = apiService.getItem()
    call.enqueue(object: Callback<ArrayList<ItemResponse>> {
      override fun onResponse(
        p0: Call<ArrayList<ItemResponse>>,
        response: Response<ArrayList<ItemResponse>>
      ) {
        if(response.isSuccessful) {
          Toast.makeText(this@HomeActivity, "Success!", Toast.LENGTH_SHORT).show()
          val result = response.body()
          itemAdapter.setData(result!!)
        }
      }

      override fun onFailure(p0: Call<ArrayList<ItemResponse>>, p1: Throwable) {
        Toast.makeText(this@HomeActivity, "Data is not found!", Toast.LENGTH_SHORT).show()

      }

    })
  }

  fun replaceActivity(activity: Activity) {
    val intent = Intent(this@HomeActivity, activity::class.java)
    startActivity(intent)
  }

}
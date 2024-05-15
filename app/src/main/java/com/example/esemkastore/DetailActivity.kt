package com.example.esemkastore

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.esemkastore.model.ItemResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText

class DetailActivity : AppCompatActivity() {
  private lateinit var tabLayout : TabLayout
  private lateinit var ivItem : ImageView
  private lateinit var tvName : TextView
  private lateinit var tvDesc : TextView
  private lateinit var tvPrice : TextView
  private lateinit var tvStock : TextView
  private lateinit var tvTotal : TextView
  private lateinit var btnPlus: MaterialButton
  private lateinit var btnMin: MaterialButton
  private lateinit var btnCart: MaterialButton
  private lateinit var etQty: TextInputEditText
  private val items by lazy { intent.getSerializableExtra("item_data") as? ItemResponse }
  private var itemId : Int = -1
  private fun initComponents() {
    tabLayout = findViewById(R.id.tb_home)
    ivItem = findViewById(R.id.iv_detail_item)
    tvName = findViewById(R.id.tv_detail_name)
    tvDesc = findViewById(R.id.tv_detail_desc)
    tvPrice = findViewById(R.id.tv_detail_price)
    tvStock = findViewById(R.id.tv_detail_stock)
    tvTotal = findViewById(R.id.tv_totalprice)
    btnPlus = findViewById(R.id.btn_plus)
    btnMin = findViewById(R.id.btn_min)
    btnCart = findViewById(R.id.btn_cart)
    etQty = findViewById(R.id.et_qty)

    items?. let {
      supportActionBar!!.title = it.name
      itemId = it.id
      Glide.with(this).load("http://10.0.2.2:5000/api/Home/Item/Photo/${it.id}")
        .override(600,600)
        .centerCrop()
        .error(R.drawable.box)
        .into(ivItem)
      tvName.setText(it.name)
      tvDesc.setText(it.description)
      tvPrice.setText("Rp. ${it.price}")
      tvStock.setText(it.stock.toString())
      val price = tvPrice.text.substring(4, tvPrice.text.length - 1)
      val priceWithoutDot = price.replace(".", "")
      Log.i("price", priceWithoutDot)
    }?: run {
      Toast.makeText(this@DetailActivity, "Data is not found", Toast.LENGTH_SHORT).show()
    }

  }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_detail)
    initComponents()
    setupListener()
    supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#E67E23")))

    loadTotal()
  }


  @SuppressLint("SetTextI18n")
  private fun setupListener() {
    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
      override fun onTabSelected(p0: TabLayout.Tab?) {
        val position : Int = p0!!.position
        if(position == 0) {
          replaceActivity(HomeActivity())
          Toast.makeText(this@DetailActivity, "Home", Toast.LENGTH_SHORT).show();
        } else if(position == 1) {
          replaceActivity(CartActivity())
          Toast.makeText(this@DetailActivity, "Cart", Toast.LENGTH_SHORT).show();
        } else if(position == 2) {
          Toast.makeText(this@DetailActivity, "History", Toast.LENGTH_SHORT).show();
        }
      }

      override fun onTabUnselected(p0: TabLayout.Tab?) {}

      override fun onTabReselected(p0: TabLayout.Tab?) {}

    })

    btnPlus.setOnClickListener {
      val currentValue = etQty.text.toString().toInt()
      etQty.setText((currentValue+1).toString())
      loadTotal()
    }

    btnMin.setOnClickListener {
      val currentValue = etQty.text.toString().toInt()
      etQty.setText((currentValue - 1).toString())
      loadTotal()
    }

    btnCart.setOnClickListener {
      if(!TextUtils.isDigitsOnly(etQty.text.toString())) {
        Toast.makeText(this@DetailActivity, "Input qty harus berupa angka!", Toast.LENGTH_SHORT).show()
      } else {
        val intent = Intent(this@DetailActivity, CartActivity::class.java)
        intent.putExtra("name", tvName.text.toString())
        intent.putExtra("count", etQty.text.toString())
        intent.putExtra("price", tvTotal.text.substring(17))
        if(itemId != -1) {
          intent.putExtra("id", itemId.toString())
        }
        startActivity(intent)

      }
    }

  }

  private fun loadTotal() {
    val textprice = tvPrice.text.substring(4, tvPrice.text.length - 1)
    val price = textprice.replace(".", "")
    val total = price.toInt() * etQty.text.toString().toInt()
    tvTotal.text = "Total Price: Rp. " + total.toString()
    Log.i("total", total.toString())
  }

  fun replaceActivity(activity: Activity) {
    val intent = Intent(this@DetailActivity, activity::class.java)
    startActivity(intent)
  }
}
package com.example.esemkastore.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.provider.Settings.Global
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.esemkastore.model.ItemResponse
import com.example.esemkastore.R
import com.example.esemkastore.retrofit.RetrofitClient
import com.example.esemkastore.service.ItemApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ItemAdapter(
  val context: Context,
  val items: ArrayList<ItemResponse>,
  val listener: OnAdapterListener
): RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

  class MyViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val ivImage = view.findViewById<ImageView>(R.id.iv_item)
    val tvName = view.findViewById<TextView>(R.id.tv_name)
    val tvDesc = view.findViewById<TextView>(R.id.tv_desc)
    val tvPrice = view.findViewById<TextView>(R.id.tv_price)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
    return MyViewHolder(view)
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val results = items[position]
    Glide.with(context).load("http://10.0.2.2:5000/api/Home/Item/Photo/${results.id}")
      .override(300,300)
      .centerCrop()
      .error(R.drawable.box)
      .into(holder.ivImage)

    holder.tvName.text = results.name
    holder.tvDesc.text = results.description
    holder.tvPrice.text = "Rp. " + results.price.toString()

    holder.itemView.setOnClickListener {
      listener.onDetail(results)
    }

  }


  public fun setData(result: List<ItemResponse>) {
    items.clear()
    items.addAll(result)
    notifyDataSetChanged()
  }

  interface OnAdapterListener {
    fun onDetail(result: ItemResponse)
  }


}
package com.example.esemkastore.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.RecyclerListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.esemkastore.R
import com.example.esemkastore.model.CartResponse

class CartAdapter(
  val context: Context,
  var items: ArrayList<CartResponse>,
  val onDeleteClickListener: (Int) -> Unit
): RecyclerView.Adapter<CartAdapter.MyViewHolder>() {
  class MyViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val ivDelete = view.findViewById<ImageView>(R.id.iv_cart_delete)
    val ivCart = view.findViewById<ImageView>(R.id.iv_cart)
    val tvName = view.findViewById<TextView>(R.id.tv_cart_name)
    val tvCount = view.findViewById<TextView>(R.id.tv_cart_count)
    val tvPrice = view.findViewById<TextView>(R.id.tv_cart_price)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
    return MyViewHolder(view)
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val results = items[position]
    holder.tvName.text = results.name
    holder.tvPrice.text = "Price: " + results.price
    holder.tvCount.text = "Count: " + results.count
    Glide.with(context).load("http://10.0.2.2:5000/api/Home/Item/Photo/${results.id}")
      .override(100,100)
      .error(R.drawable.box)
      .centerCrop()
      .into(holder.ivCart)

    holder.ivDelete.setOnClickListener {
      onDeleteClickListener.invoke(holder.adapterPosition)

    }
  }

  public fun setData(result: List<CartResponse>) {
    items.clear()
    items.addAll(result)
    notifyDataSetChanged()
  }

}
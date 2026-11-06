package com.example.flowapp

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val context: Activity, val productList: List<Ticket>):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.each_ticket,parent,false)
        return MyViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = productList[position]
        holder.ticketNumber.text = currentItem.toString()
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var ticketImage: ImageView = itemView.findViewById(R.id.ticketImage)
        var ticketNumber: TextView = itemView.findViewById(R.id.ticketNumber)
        var date: TextView = itemView.findViewById(R.id.date_time)
        var paymentStat: TextView = itemView.findViewById(R.id.ticketPaymentStatus)
    }
}
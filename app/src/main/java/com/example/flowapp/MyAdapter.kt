package com.example.flowapp

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val context: Activity, val productList: List<Ticket>):
    RecyclerView.Adapter<MyAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.each_ticket,parent,false)
        return MyViewer(.it)
    }
}
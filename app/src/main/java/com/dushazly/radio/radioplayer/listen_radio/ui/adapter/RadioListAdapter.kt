package com.dushazly.radio.radioplayer.listen_radio.ui.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.dushazly.radio.radioplayer.R
import com.dushazly.radio.radioplayer.listen_radio.ui.RecyclerViewOnItemClick
import com.dushazly.radio.radioplayer.model.dto.RadioChannelModel

import java.util.ArrayList


class RadioListAdapter(private val activity: Activity, shoutcasts: List<RadioChannelModel>, private val onItemClick: RecyclerViewOnItemClick) :
    RecyclerView.Adapter<RadioListAdapter.MyViewHolder>() {


    private var shoutcasts = ArrayList<RadioChannelModel>()

    init {
        this.shoutcasts = shoutcasts as ArrayList<RadioChannelModel>
        print("shoutcasts.size >>" + shoutcasts.size)
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return MyViewHolder(itemView)    }

    override fun getItemCount(): Int {
       return shoutcasts.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val radioChannel = shoutcasts?.get(position)
        val channelName = radioChannel?.title
        print(" channelName " + channelName)
        holder.channeltextView?.text = channelName!!

        holder.itemView.setOnClickListener { v ->
            if (channelName != null) {
                onItemClick?.onItemClick(radioChannel)
            }
        }
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var channeltextView: TextView? = null


        init {
            channeltextView = view.findViewById(R.id.text)
        }
    }

}

package com.dushazly.radio.radioplayer.listen_radio.ui

import com.dushazly.radio.radioplayer.model.dto.RadioChannelModel


interface RecyclerViewOnItemClick {
//    fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long)
    fun onItemClick(source: RadioChannelModel)
}
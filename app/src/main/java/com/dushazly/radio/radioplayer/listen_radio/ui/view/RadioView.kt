package com.dushazly.radio.radioplayer.listen_radio.ui.view

import com.dushazly.radio.radioplayer.model.dto.RadioResponse
import com.dushazly.radio.radioplayer.view.MvpView


interface RadioView : MvpView {

    fun showError(message: String)


    fun showAllMusicActivities(list: RadioResponse)

}

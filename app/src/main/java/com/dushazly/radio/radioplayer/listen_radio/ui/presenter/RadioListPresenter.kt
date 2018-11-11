package com.dushazly.radio.radioplayer.tabs.channels.ui.presenter

import com.dushazly.radio.radioplayer.listen_radio.ui.view.RadioView
import com.dushazly.radio.radioplayer.presenter.BasePresenter


abstract class RadioListPresenter : BasePresenter<RadioView>() {

    abstract fun displayAllRadioStation()


}

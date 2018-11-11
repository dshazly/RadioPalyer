package com.dushazly.radio.radioplayer.applogin.model.cloud


import com.dushazly.radio.radioplayer.model.dto.RadioResponse
import io.reactivex.Observable



interface GetRadioRepo {


    fun getMusic(): Observable<RadioResponse>


}

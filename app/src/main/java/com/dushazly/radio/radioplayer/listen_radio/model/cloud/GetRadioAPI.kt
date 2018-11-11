package com.dushazly.radio.radioplayermodel.cloud



import com.dushazly.radio.radioplayer.model.dto.RadioResponse
import io.reactivex.Observable
import retrofit2.http.*


interface GetRadioAPI  {

    

    @GET("o/music.json?alt=media&token=64ac05a8-2f23-4cef-b25c-b488519b0650")
    fun getMusic(): Observable<RadioResponse>




}

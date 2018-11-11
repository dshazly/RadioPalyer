package com.dushazly.radio.radioplayer.model.cloud

import com.dushazly.radio.radioplayer.applogin.model.cloud.GetRadioRepo
import com.dushazly.radio.radioplayer.model.dto.RadioResponse
import com.dushazly.radio.radioplayer.repo.cloud.BaseCloudRepo
import com.dushazly.radio.radioplayermodel.cloud.GetRadioAPI

import io.reactivex.Observable


open class GetRadioCloudRepoImpl : BaseCloudRepo(), GetRadioRepo {
    override fun getMusic(): Observable<RadioResponse> {
        return create(GetRadioAPI::class.java).getMusic()
    }


}

package com.dushazly.radio.radioplayer.model.repo

import com.dushazly.radio.radioplayer.error.ErrorManager
import com.dushazly.radio.radioplayer.model.cloud.GetRadioCloudRepoImpl
import com.dushazly.radio.radioplayer.model.dto.RadioResponse


import io.reactivex.Observable



class GetRadioRepoImpl {

    internal var getMusicCloudRepo: GetRadioCloudRepoImpl? = null

    fun getMusic(): Observable<RadioResponse> {
        return ErrorManager.wrap(getMusicCloudRepo().getMusic())
    }

    fun getMusicCloudRepo(): GetRadioCloudRepoImpl {
        if (getMusicCloudRepo == null) {
            getMusicCloudRepo = GetRadioCloudRepoImpl()
        }
        return getMusicCloudRepo as GetRadioCloudRepoImpl
    }

    fun setMusicCloudRepo(appLoginCloudRepo: GetRadioCloudRepoImpl) {
        this.getMusicCloudRepo = appLoginCloudRepo
    }
}
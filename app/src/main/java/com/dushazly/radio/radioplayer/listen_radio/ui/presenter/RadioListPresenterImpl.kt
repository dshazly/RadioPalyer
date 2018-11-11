package com.orange.hubme.tabs.channels.ui.presenter


import com.dushazly.radio.radioplayer.error.AppException
import com.dushazly.radio.radioplayer.model.dto.RadioResponse
import com.dushazly.radio.radioplayer.model.repo.GetRadioRepoImpl
import com.dushazly.radio.radioplayer.tabs.channels.ui.presenter.RadioListPresenter
import io.reactivex.Scheduler
import io.reactivex.exceptions.CompositeException
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


class RadioListPresenterImpl
constructor(private val schedulers: Scheduler) : RadioListPresenter() {
    internal var radioRepo = GetRadioRepoImpl()

    override fun displayAllRadioStation() {
        if (!isViewAttached) {
            return
        }
        addDisposable(radioRepo.getMusic().observeOn(schedulers).subscribeOn(Schedulers.io()).subscribeWith(object : DisposableObserver<RadioResponse>() {
            override fun onNext(musicResponse: RadioResponse) {
                if (!isViewAttached) {
                    return
                }
                getView()!!.showAllMusicActivities(musicResponse)

            }

            override fun onError(e: Throwable) {
                if (!isViewAttached) {
                    return
                }

                getView()!!.showError(e.message!!)




            }

            override fun onComplete() {

            }
        }))

    }

}

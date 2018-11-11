package com.dushazly.radio.radioplayer.presenter


import com.dushazly.radio.radioplayer.view.MvpView

import io.reactivex.disposables.Disposable

/**
 * Created by Eslam Hussein on 5/14/16.
 */
interface MvpPresenter< P :MvpView> {

    /**
     * Called when an `MvpView` is attached to this presenter.
     *
     * @param view The attached `MvpView`
     */
    fun onAttach(view: P)

    /**
     * Called when the view is resumed according to Android components
     * NOTE: this method will only be called for presenters that override it.
     */
    fun onResume()

    fun addDisposable(disposable: Disposable)
    /**
     * Called when an `MvpView` is detached from this presenter.
     */
    fun onDetach()

}

package com.dushazly.radio.radioplayer.presenter


import com.dushazly.radio.radioplayer.view.MvpView

import java.lang.ref.WeakReference

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created Eslam Hussein on 5/14/16.
 */
open abstract class BasePresenter<P : MvpView> : MvpPresenter<P> {



    private var viewRef: WeakReference<P>? = null
    private var compositeDisposable: CompositeDisposable? = null

    /**
     * @return True if the view this presenter is attached to still exists and not garbage collected
     * since we are holding it through a `WeakReference`
     */
    public var isViewAttached: Boolean = false
        get() = viewRef != null && viewRef!!.get() != null



    override fun onAttach(view: P) {
        viewRef = WeakReference(view)
        compositeDisposable = CompositeDisposable()
    }

    protected fun getView(): P? {
        return viewRef?.get()
    }


    override fun addDisposable(disposable: Disposable) {
        if (compositeDisposable == null)
            compositeDisposable = CompositeDisposable()
        compositeDisposable!!.add(disposable)
    }

    override fun onResume() {
        // Not mandatory for all views, if views are interested in receiving this event, they should
        // override this method
    }

    override fun onDetach() {
        if (viewRef != null) {
            viewRef!!.clear()
            viewRef = null
        }
        if (compositeDisposable != null)
            compositeDisposable!!.clear()

    }


}

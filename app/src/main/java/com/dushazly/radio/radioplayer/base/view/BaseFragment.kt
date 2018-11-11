package com.dushazly.radio.radioplayer.view

import android.os.Bundle
import android.support.v4.app.Fragment
import com.dushazly.radio.radioplayer.presenter.MvpPresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

abstract class BaseFragment<V: MvpView,T : MvpPresenter<V>> : Fragment(),MvpView{
    private var presenter: T? = null


    protected fun getPresenter(): T {
        if (presenter == null)
            presenter = createPresenter()
        if (presenter == null)
            throw IllegalStateException("createPresenter() implementation returns null!")
        return presenter as T
    }


    protected abstract fun createPresenter(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            getPresenter().onAttach(view = this as V)

//        getPresenter().onAttach(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        initView(savedInstanceState)
    }



    override fun onResume() {
        super.onResume()
        getPresenter().onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        getPresenter().onDetach()
    }


    override fun onStart() {

        super.onStart()

        EventBus.getDefault().register(this)
    }

    @Subscribe
    abstract fun  onEvent(status: String)

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)

    }
}

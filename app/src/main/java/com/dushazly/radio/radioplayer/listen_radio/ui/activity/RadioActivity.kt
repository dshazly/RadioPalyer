package com.dushazly.radio.radioplayer.listen_radio.ui.activity

import android.os.Bundle

import butterknife.ButterKnife
import com.dushazly.radio.radioplayer.R
import com.dushazly.radio.radioplayer.listen_radio.ui.fragment.RadioFragment
import com.dushazly.radio.radioplayer.view.BaseActivity

class RadioActivity : BaseActivity() {


    override val layoutResId: Int
        get() = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        ButterKnife.bind(this)
        val ft = supportFragmentManager
                .beginTransaction()
                .replace(R.id.radio_fragment_container, RadioFragment.newInstance("", ""), "Radio fragment")
        if (!supportFragmentManager.isStateSaved) {
            ft.commit()
        } else if (true) {
            ft.commitAllowingStateLoss()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    public override fun onStop() {

        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

    }


    override fun onBackPressed() {

        finish()
    }


}

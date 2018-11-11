package com.dushazly.radio.radioplayer.player.scheduler

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log

import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.Util
import com.dushazly.radio.radioplayer.player.manager.RadioManager

class RadioScheduler : JobService() {//implements Player.EventListener, AudioManager.OnAudioFocusChangeListener

    private val TAG = "RadioScheduler"

    private val BANDWIDTH_METER = DefaultBandwidthMeter()
    internal lateinit var radioManager: RadioManager
    internal var action: String? = null
    internal lateinit var streamURL: String
    private var jobCancelled = false

    private val userAgent: String
        get() = Util.getUserAgent(this, javaClass.getSimpleName())


    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Thread(Runnable {
                if (jobCancelled) {
                    return@Runnable
                }

                try {
                    initRadioManager()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
        }).start()


        return true
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        Log.i(TAG, "on stop job: " + jobParameters.getJobId())
        jobCancelled = true;
        radioManager.unbind()
        return false

    }

    private fun initRadioManager() {
        radioManager = RadioManager.with(this)
        radioManager.bind()
    }

    override fun onCreate() {
        super.onCreate()

    }

    fun PlayOrPause(streamURL: String) {
        initRadioManager()

        IS_SERVICE_RUNNING = true
        radioManager.playOrPause(streamURL)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent == null) {
            stopForeground(true)
            stopSelf()
        }
        try {
            if (intent!!.action != null) {
                if (intent.action == "PlayOrPause") {
                    Log.d("TAG", "onStartCommand: ACTION IS " + intent.action!!)
                    streamURL = intent.getStringExtra("PlayOrPause")
                    Log.d("TAG", "onStartCommand: getStringExtra IS $streamURL")
                    PlayOrPause(streamURL)
                }

                if (intent.action == "playTrigger") {
                    Log.d("TAG", "onStartCommand: ACTION IS " + intent.action!!)
                    streamURL = intent.getStringExtra("playTrigger")
                    Log.d("TAG", "onStartCommand: getStringExtra IS $streamURL")
                    PlayOrPause(streamURL)
                }
            }
        } catch (ex: NullPointerException) {
            stopSelf()
            stopForeground(true)
        } finally {
            stopForeground(true)
        }
        return Service.START_STICKY

        //        return START_NOT_STICKY;
    }

//    override fun onUnbind(intent: Intent): Boolean {
//        radioManager.unbind()
//        IS_SERVICE_RUNNING = false
//        return super.onUnbind(intent)
//    }

//    override fun onRebind(intent: Intent) {
//        initRadioManager()
//    }

    override fun onDestroy() {
//        radioManager.unbind()
        IS_SERVICE_RUNNING = false
        stopForeground(true)
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        IS_SERVICE_RUNNING = false
        stopForeground(true)
    }

    companion object {

        val ACTION_PLAY = "com.dushazly.radio.radioplayer.player.ACTION_PLAY"
        val ACTION_PAUSE = "com.dushazly.radio.radioplayer.player.ACTION_PAUSE"
        val ACTION_STOP = "com.dushazly.radio.radioplayer.player.ACTION_STOP"
        var IS_SERVICE_RUNNING = false
    }
}

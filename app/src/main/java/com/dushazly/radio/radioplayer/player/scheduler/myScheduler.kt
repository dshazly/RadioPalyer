package com.dushazly.radio.radioplayer.player.scheduler

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.PersistableBundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.TextUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.dushazly.radio.radioplayer.R
import com.dushazly.radio.radioplayer.player.MediaNotificationManager
import com.dushazly.radio.radioplayer.player.PlaybackStatus
import com.dushazly.radio.radioplayer.player.service.RadioService
import org.greenrobot.eventbus.EventBus


class myScheduler : JobService(), ExoPlayer.EventListener, AudioManager.OnAudioFocusChangeListener {


    private var handler: Handler? = null
    private val BANDWIDTH_METER = DefaultBandwidthMeter()
    private var exoPlayer: SimpleExoPlayer? = null
    var mediaSession: MediaSessionCompat? = null
        private set
    private var transportControls: MediaControllerCompat.TransportControls? = null

    private var onGoingCall = false
    private var telephonyManager: TelephonyManager? = null

    private var wifiLock: WifiManager.WifiLock? = null

    private var audioManager: AudioManager? = null

    private var notificationManager: MediaNotificationManager? = null

    var status: String? = null
        private set

    private var strAppName: String? = null
    private var strLiveBroadcast: String? = null
    private var streamUrl: String? = null

    private val becomingNoisyReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            pause()
        }
    }

    private val phoneStateListener = object : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, incomingNumber: String) {

            if (state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING) {

                if (!isPlaying) return

                onGoingCall = true
                stop()

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                if (!onGoingCall) return

                onGoingCall = false
                resume()
            }
        }
    }

    private val mediasSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPause() {
            super.onPause()

            pause()
        }

        override fun onStop() {
            super.onStop()

            stop()

            notificationManager!!.cancelNotify()
        }

        override fun onPlay() {
            super.onPlay()

            resume()
        }
    }

    val isPlaying: Boolean
        get() = this.status == PlaybackStatus.PLAYING

    private val userAgent: String
        get() = Util.getUserAgent(this, javaClass.getSimpleName())

    override fun onCreate() {
        super.onCreate()

        strAppName = resources.getString(R.string.app_name)
        strLiveBroadcast = resources.getString(R.string.live_broadcast)

        onGoingCall = false

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

//        notificationManager = MediaNotificationManager(this)

        wifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            .createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock")

        mediaSession = MediaSessionCompat(this, javaClass.getSimpleName())
        transportControls = mediaSession!!.controller.transportControls
        mediaSession!!.isActive = true
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession!!.setMetadata(MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "...")
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, strAppName)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, strLiveBroadcast)
            .build())
        mediaSession!!.setCallback(mediasSessionCallback)

        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)

        handler = Handler()
        val bandwidthMeter = DefaultBandwidthMeter()
        val trackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(trackSelectionFactory)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(applicationContext, trackSelector)


        registerReceiver(becomingNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))

        status = PlaybackStatus.IDLE

    }

    override fun onStartJob(jobParameters: JobParameters?): Boolean {
         var bundle = jobParameters?.getExtras()

//            val action = intent.action


            val result = audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                stop()

                return true
            }

            if (bundle?.getString( RadioService.ACTION_PLAY)!!.equals(RadioService.ACTION_PLAY, ignoreCase = true)) {

                transportControls!!.play()

            } else if (bundle?.getString( RadioService.ACTION_PAUSE)!!.equals(RadioService.ACTION_PAUSE, ignoreCase = true)) {

                transportControls!!.pause()

            } else if (bundle?.getString( RadioService.ACTION_STOP)!!.equals(RadioService.ACTION_STOP, ignoreCase = true)) {

                transportControls!!.stop()

            }

            return true

//        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return false
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
    }

    override fun onSeekProcessed() {
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        EventBus.getDefault().post(PlaybackStatus.ERROR)
    }

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onPositionDiscontinuity(reason: Int) {
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        if (!timeline?.isEmpty!!) {
            exoPlayer?.removeListener(this);
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_BUFFERING -> status = PlaybackStatus.LOADING
            Player.STATE_ENDED -> status = PlaybackStatus.STOPPED
            Player.STATE_IDLE -> status = PlaybackStatus.IDLE
            Player.STATE_READY -> status = if (playWhenReady) PlaybackStatus.PLAYING else PlaybackStatus.PAUSED
            else -> status = PlaybackStatus.IDLE
        }

        if (status != PlaybackStatus.IDLE)
            notificationManager!!.startNotify(status!!)

        EventBus.getDefault().post(status)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {

                exoPlayer?.volume = 0.8f

                resume()
            }

            AudioManager.AUDIOFOCUS_LOSS ->

                stop()

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->

                if (isPlaying) pause()

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->

                if (isPlaying)
                    exoPlayer?.volume = 0.1f
        }
    }

    fun play(streamUrl: String) {

        this.streamUrl = streamUrl

        if (wifiLock != null && !wifiLock!!.isHeld) {

            wifiLock!!.acquire()

        }


        val dataSourceFactory = DefaultDataSourceFactory(applicationContext, userAgent, BANDWIDTH_METER)

        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(streamUrl))

        exoPlayer?.prepare(mediaSource)

        exoPlayer?.addListener(this)

        exoPlayer?.playWhenReady = true

    }

    fun resume() {

        if (streamUrl != null)
            play(streamUrl!!)
    }

    fun pause() {

        exoPlayer?.playWhenReady = false

        audioManager!!.abandonAudioFocus(this)
        wifiLockRelease()
    }

    fun stop() {

        exoPlayer?.stop()

        audioManager!!.abandonAudioFocus(this)
        wifiLockRelease()
    }

    fun playOrPause(url: String) {

        if (streamUrl != null && streamUrl == url) {
            println("isPlaying >> " + isPlaying)
            if (isPlaying ) {//!isPlaying
                play(streamUrl!!)
            } else  {
                pause()
            }

        } else {
            if (isPlaying) {
                pause()
            }
            play(url)
        }
    }

    private fun wifiLockRelease() {

        if (wifiLock != null && wifiLock!!.isHeld) {

            wifiLock!!.release()
        }
    }

    companion object {

        val ACTION_PLAY = "com.dushazly.radio.radioplayer.player.ACTION_PLAY"
        val ACTION_PAUSE = "com.dushazly.radio.radioplayer.player.ACTION_PAUSE"
        val ACTION_STOP = "com.dushazly.radio.radioplayer.player.ACTION_STOP"
    }

}
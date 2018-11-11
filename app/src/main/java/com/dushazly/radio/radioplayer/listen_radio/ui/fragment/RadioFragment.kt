package com.dushazly.radio.radioplayer.listen_radio.ui.fragment

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.*
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import com.dushazly.radio.radioplayer.R
import com.dushazly.radio.radioplayer.listen_radio.ui.view.RadioView
import com.dushazly.radio.radioplayer.player.PlaybackStatus
import com.dushazly.radio.radioplayer.player.scheduler.RadioScheduler
import com.dushazly.radio.radioplayer.tabs.channels.ui.presenter.RadioListPresenter

import com.dushazly.radio.radioplayer.listen_radio.ui.adapter.RadioListAdapter
import com.orange.hubme.tabs.channels.ui.presenter.RadioListPresenterImpl
import com.dushazly.radio.radioplayer.listen_radio.ui.RecyclerViewOnItemClick
import com.dushazly.radio.radioplayer.model.dto.RadioChannelModel
import com.dushazly.radio.radioplayer.model.dto.RadioResponse
import com.dushazly.radio.radioplayer.view.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.content.ComponentName
import android.os.Handler
import android.os.Message
import android.support.annotation.ColorRes
import android.support.design.widget.Snackbar
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.ref.WeakReference


class RadioFragment : BaseFragment<RadioView, RadioListPresenter>(), RadioView, RecyclerViewOnItemClick {

    private val TAG = "RadioFragment"
    @BindView(R.id.playTrigger)
    internal var trigger: ImageButton? = null

    @BindView(R.id.radioRecyclerView)
    internal var radioRecyclerView: RecyclerView? = null

    @BindView(R.id.name)
    internal var textView: TextView? = null

    @BindView(R.id.sub_player)
    internal var subPlayer: View? = null

    @BindView(R.id.relative_layout_radio_list)
    private lateinit var relativeLayoutchannelList: RelativeLayout

    internal lateinit var streamURL: String
    internal var isWiFi: Int = 0
    private var mServiceComponent: ComponentName? = null

    private var mRadioList: RadioResponse? = null
    internal var JOB_ID = 0

    private var mRadioListAdapter: RadioListAdapter? = null
    private var mLayoutManager: LinearLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleJob_(activity!!)
//        mBroadcastReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent?) {
//                if (intent != null && intent.extras != null) {
//                    if (intent.hasExtra("status")) {
//                        val action = intent.extras!!.getString("status")
//                        onEvent(action)
//                    }
//                }
//            }
//        }
//        LocalBroadcastManager.getInstance(activity!!).registerReceiver(
//            mBroadcastReceiver,
//            IntentFilter("mIntent")
//        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_radio, container, false)
    }

    fun getAllRadio() {
        getPresenter().displayAllRadioStation()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trigger = view.findViewById(R.id.playTrigger)

        radioRecyclerView = view.findViewById(R.id.radioRecyclerView)
        mLayoutManager = LinearLayoutManager(getActivity())
        radioRecyclerView!!.layoutManager = mLayoutManager
        radioRecyclerView!!.addItemDecoration(DividerItemDecoration(getActivity()!!, LinearLayoutManager.VERTICAL))
        radioRecyclerView!!.itemAnimator = DefaultItemAnimator()
        textView = view.findViewById(R.id.name)
        subPlayer = view.findViewById(R.id.sub_player)
        relativeLayoutchannelList = view.findViewById(R.id.relative_layout_radio_list)
        getAllRadio()
        mRadioList = RadioResponse()


        val cm = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo

        if(activeNetwork != null) {
            if (activeNetwork!!.type == ConnectivityManager.TYPE_WIFI) {
                isWiFi = ConnectivityManager.TYPE_WIFI
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                isWiFi = ConnectivityManager.TYPE_MOBILE
            }
        }else{
         Toast.makeText(activity!!,"No internet Connection ..",Toast.LENGTH_LONG).show()

        }


        trigger?.setOnClickListener { view ->

            if (TextUtils.isEmpty(streamURL)) {
                Toast.makeText(context, "", Toast.LENGTH_LONG).show()
            } else {
                println("streamURL is " + streamURL)
                val service = Intent(activity!!, RadioScheduler::class.java)
                service.setAction("playTrigger");
                service.putExtra("playTrigger", streamURL);
                activity!!.startService(service);
//                startPlay()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
//        LocalBroadcastManager.getInstance(activity!!).registerReceiver(
//            mBroadcastReceiver,
//            IntentFilter("mIntent")
//        )

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    fun scheduleJob_(context: Context) {
        val componentName = ComponentName(activity!!, RadioScheduler::class.java)
        val info = JobInfo.Builder(JOB_ID++, componentName)
            .setRequiresCharging(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic((15 * 60 * 1000).toLong())
            .build()

        val scheduler = activity!!.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler?
        val resultCode = scheduler!!.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled")
        } else {
            Log.d(TAG, "Job scheduling failed")
        }
    }

//    @SuppressLint("MissingPermission")
//    fun scheduleJob(v: View) {
//        val componentName = ComponentName(activity!!, RadioScheduler::class.java)
//        val info = JobInfo.Builder(123, componentName)
//            .setRequiresCharging(true)
//            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//            .setPersisted(true)
//            .setPeriodic((15 * 60 * 1000).toLong())
//            .build()
//
//        val scheduler = activity!!.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler?
//        val resultCode = scheduler!!.schedule(info)
//        if (resultCode == JobScheduler.RESULT_SUCCESS) {
//            Log.d(TAG, "Job scheduled")
//        } else {
//            Log.d(TAG, "Job scheduling failed")
//        }
//    }
//
//    fun cancelJob(v: View) {
//        val scheduler = activity!!.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler?
//        scheduler!!.cancel(123)
//        Log.d(TAG, "Job cancelled")
//    }

    @Subscribe
    override fun onEvent(status: String) {
        println("onEvent fun status is " + status)
        when (status) {

            PlaybackStatus.LOADING -> {
//                            Toast.makeText(activity!!,"Loading ..",Toast.LENGTH_LONG).show()
            }

            PlaybackStatus.ERROR ->{

                            Toast.makeText(activity!!, R.string.no_stream, Toast.LENGTH_SHORT).show()// returns an object which

            }


            PlaybackStatus.IDLE ->{

//                            Toast.makeText(activity!!, "IDLE", Toast.LENGTH_SHORT).show()

            }


        }// loading

        trigger?.setImageResource(
            if (status == PlaybackStatus.PLAYING)
                R.drawable.ic_pause_black
            else
                R.drawable.ic_play_arrow_black
        )

    }



    override fun showError(message: String) {
        Snackbar.make(relativeLayoutchannelList!!, message, Snackbar.LENGTH_LONG).show()

    }



    override fun showAllMusicActivities(list: RadioResponse) {
        if (!list.music?.isEmpty()!!) {
            mRadioList = RadioResponse()
            mRadioList = list
            mRadioListAdapter = RadioListAdapter(
                activity!!,
                mRadioList?.music!!,
                this
            )
            radioRecyclerView!!.adapter = mRadioListAdapter


        }
    }


    override fun createPresenter(): RadioListPresenter {
        return RadioListPresenterImpl(AndroidSchedulers.mainThread())

    }

    override fun onStart() {

        super.onStart()

    }

    override fun onStop() {
//        if (mBroadcastReceiver != null) {
//            LocalBroadcastManager.getInstance(activity!!)
//                .unregisterReceiver(mBroadcastReceiver)
//        }
        super.onStop()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onItemClick(source: RadioChannelModel) {
        textView?.text = source.title

        subPlayer?.visibility = View.VISIBLE

        streamURL = source?.site!! + source.source!!

//        startPlay()

//        radioManager.playOrPause(streamURL)

        val service = Intent(activity!!, RadioScheduler::class.java)
        service.setAction("PlayOrPause");
        service.putExtra("PlayOrPause", streamURL);
        activity!!.startService(service);
    }

    fun startPlay(){
        val service = Intent(activity!!, RadioScheduler::class.java)
        service.setAction("PlayOrPause");
        service.putExtra("PlayOrPause", streamURL);
        activity!!.startService(service);
    }
    companion object {

        fun newInstance(param1: String, param2: String): RadioFragment {
            val fragment = RadioFragment()
            val args = Bundle()
            fragment.setArguments(args)
            return fragment
        }
    }

}

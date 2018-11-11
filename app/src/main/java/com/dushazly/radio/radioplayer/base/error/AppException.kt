package com.dushazly.radio.radioplayer.error

import android.support.annotation.IntDef
import com.dushazly.radio.radioplayer.R


import com.dushazly.radio.radioplayer.util.JsonUtil
import com.dushazly.radio.radioplayer.util.TextUtils
import retrofit2.HttpException


import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class AppException @JvmOverloads constructor(@param:ErrorCode @get:ErrorCode
                                             val errorCode: Int, val errorMessage: String, original: Throwable? = null) : Exception(original) {

    @IntDef(NETWORK_ERROR, NO_DATA_ERROR, UNKNOWN_ERROR, API_ERROR)
    annotation class ErrorCode

    companion object {

        const val NETWORK_ERROR = 1
        const val NO_DATA_ERROR = 2
        const val UNKNOWN_ERROR = 3
        const val API_ERROR = 4

        fun adapt(t: Throwable): Throwable {
            return if (t is HttpException) {
                try {
                    val message = JsonUtil.parsePureString(t.response().errorBody()!!.string(), "message")
                    AppException(t.response().code(), message!!, t)

                } catch (e: IOException) {
                    e.printStackTrace()
                    AppException(UNKNOWN_ERROR, TextUtils.getString(R.string.some_thing_went_wrong), t)

                }

            } else if (t is UnknownHostException || t is SocketException || t is SocketTimeoutException) {
                AppException(NETWORK_ERROR, TextUtils.getString(R.string.no_internet_connection), t)
            } else {
                AppException(UNKNOWN_ERROR, TextUtils.getString(R.string.some_thing_went_wrong), t)
            }
        }
    }

}

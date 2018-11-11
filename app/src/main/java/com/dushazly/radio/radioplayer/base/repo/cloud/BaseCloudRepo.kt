package com.dushazly.radio.radioplayer.repo.cloud


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

import java.util.HashMap
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Eslam Hussein on 5/14/16.
 */
open class BaseCloudRepo {

    private val servicesMap: MutableMap<Class<*>, Any>

    init {
        servicesMap = HashMap()
    }


    /* Commented By Mahmoud Hesham :D
    *
    * No Refresh Token
    *
    */



    protected fun <T> create(clazz: Class<T>): T {
        val service : T = retrofit(true).create(clazz)
        return service
    }

    /* Commented By Mahmoud Hesham :D
    *
    *Refresh Token
    *
    */
    fun <S> createServiceRefreshToken(
            serviceClass: Class<S>): S {

        return retrofit(false).create(serviceClass)
    }


    private fun retrofit(withInterceptor: Boolean): Retrofit {
//        val logging = HttpLoggingInterceptor()
//        logging.level = HttpLoggingInterceptor.Level.HEADERS
//        logging.level = HttpLoggingInterceptor.Level.BODY
        //        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(CloudConfig.CONNECTION_TIMEOUT.toLong(), TimeUnit.MINUTES)
                .readTimeout(CloudConfig.READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
//                .addInterceptor(logging)
                .writeTimeout(CloudConfig.WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)

        //
        //        if (withInterceptor) {
        //            okHttpClient.addInterceptor(new TokenInterceptor());
        //        }
        //        GsonBuilder gsonBuilder = new GsonBuilder();


        return Retrofit.Builder()
                .baseUrl(CloudConfig.TEST_BASE_URL).client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    companion object {
        private val BASE_CLOUD_TAG = "BaseCloudRepo"
        private val retrofitIntercept: Retrofit? = null
        private val retrofitWithoutInterceptor: Retrofit? = null
    }


}

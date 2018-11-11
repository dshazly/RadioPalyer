package com.dushazly.radio.radioplayer.util


import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.dushazly.radio.radioplayer.error.AppException

import org.json.JSONException
import org.json.JSONObject

import java.lang.reflect.Type



object JsonUtil {

    @Throws(AppException::class)
    fun <T> parseJson(jsonString: String, clazz: Class<T>): T {
        try {
            val gson = Gson()
            return gson.fromJson(jsonString, clazz) as T

        } catch (e: Exception) {
            throw e
        }

    }

    @Throws(AppException::class)
    fun <T> parseJson(jsonString: String, clazz: Type): T? {

        try {
            val gson = Gson()

            return gson.fromJson<Any>(jsonString, clazz) as T

        } catch (e: Exception) {
            throw e
        }

    }

    fun objectToString(clazz: Any): String {
        val gson = Gson()
        return gson.toJson(clazz)

    }

    fun parsePureString(jsonLine: String, key: String): String? {
        try {
            val jsonObject = JSONObject(jsonLine)
            return jsonObject.getString(key)
        } catch (e: JSONException) {
            return null
        }

    }
}


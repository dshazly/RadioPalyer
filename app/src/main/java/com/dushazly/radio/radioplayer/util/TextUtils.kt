package com.dushazly.radio.radioplayer.util


import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Patterns

import com.dushazly.radio.radioplayer.util.TextUtils.EMAIL

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Eslam Hussein on a10/30/2016.
 */

object TextUtils {

    private val EMPTY_STRING_PATTERN = "^$|\\s+"

    private var matcher: Matcher? = null


    private val EMAIL = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    )

    @VisibleForTesting
    fun getString(@StringRes resId: Int): String {
//        return HubMeApp.get().getString(resId)
return ""
    }

    fun validateEmail(email: String): Boolean {
        matcher = Patterns.EMAIL_ADDRESS.matcher(email)
        return matcher!!.matches()
    }

    fun validate_Email1(email: String): Boolean {
        return EMAIL.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6 && password.length <= 20
    }


    fun isEmptyString(str: String?): Boolean {
        return if (str == null || str.length == 0 ||
                str.matches(EMPTY_STRING_PATTERN.toRegex())) {
            true
        } else false
    }

    fun coloringString(text: String, @ColorRes color: Int): SpannableStringBuilder {

        val sb = SpannableStringBuilder(text)

        // Span to set text color to some RGB value
//        val fcs = ForegroundColorSpan(HubMeApp.get().resources.getColor(color))

        // Set the text color for first 4 characters
//        sb.setSpan(fcs, 0, text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        return sb
    }


    fun isRegxMatched(text: String, regx: String): Boolean {
        val r = Pattern.compile(regx)
        val m = r.matcher(text)
        return if (m.find()) {
            true
        } else {
            false
        }
    }

    fun containsOnlyNumbers(str: String): Boolean {
        for (i in 0 until str.length) {
            if (!Character.isDigit(str[i]))
                return false
        }
        return true
    }
}

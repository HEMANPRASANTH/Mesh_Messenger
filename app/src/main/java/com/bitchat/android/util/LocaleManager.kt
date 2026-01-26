package com.bitchat.android.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList

import java.util.Locale

object LocaleManager {
    private const val KEY_LANGUAGE = "app_language"

    fun setLocale(context: Context, languageCode: String): Context {
        persistLanguage(context, languageCode)
        return updateBaseContext(context, languageCode)
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    private fun persistLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun updateBaseContext(context: Context): Context {
        val language = getLanguage(context)
        return updateBaseContext(context, language)
    }

    private fun updateBaseContext(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        return context.createConfigurationContext(config)
    }
}

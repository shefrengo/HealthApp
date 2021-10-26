package com.shefrengo.health.utils;
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList

import java.util.LinkedHashSet
import java.util.Locale

import androidx.annotation.RequiresApi

import android.os.Build.VERSION_CODES.N
import com.shefrengo.health.utils.extentions.getSharedPrefInstance



class LocaleManager(context: Context) {


    val language: String?
        get() = getSharedPrefInstance().getStringValue(LANGUAGE_KEY, LANGUAGE_ENGLISH)

    fun setLocale(c: Context): Context {
        return updateResources(c, language)
    }

    fun setNewLocale(c: Context, language: String): Context {
        persistLanguage(language)
        return updateResources(c, language)
    }

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(language: String) {
        // use commit() instead of apply(), because sometimes we kill the application process
        // immediately that prevents apply() from finishing
        getSharedPrefInstance().setValue(LANGUAGE_KEY,language)
    }

    private fun updateResources(context: Context, language: String?): Context {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)

        val res = context.resources
        val config = Configuration(res.configuration)
        context = if (Build.VERSION.SDK_INT >= N) {
            setLocaleForApi24(config, locale)
            context.createConfigurationContext(config)
        } else {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        }
        return context
    }

    @RequiresApi(api = N)
    private fun setLocaleForApi24(config: Configuration, target: Locale) {
        val set = LinkedHashSet<Locale>()
        // bring the target locale to the front of the list
        set.add(target)

        val all = LocaleList.getDefault()
        for (i in 0 until all.size()) {
            // append other locales supported by the user
            set.add(all.get(i))
        }

        val locales = set.toTypedArray()
        //config.locales = LocaleList(*locales)
    }

    companion object {

        val LANGUAGE_ENGLISH = "en"
        val LANGUAGE_UKRAINIAN = "uk"
        val LANGUAGE_RUSSIAN = "ru"
        private val LANGUAGE_KEY = "language_key"

        fun getLocale(res: Resources): Locale {
            val config = res.configuration
            return if (Build.VERSION.SDK_INT >= N) config.locales.get(0) else config.locale
        }
    }
}
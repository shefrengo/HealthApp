package com.shefrengo.health

import android.app.Dialog
import android.content.Context

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.shefrengo.health.Constants.SharedPref.LANGUAGE
import com.shefrengo.health.utils.LocaleManager

import com.shefrengo.health.utils.extentions.getSharedPrefInstance
import com.shefrengo.health.utils.SharedPrefUtils
import com.squareup.okhttp.OkHttpClient
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class WooBoxApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        getSharedPrefInstance().apply {
            appTheme = getIntValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
            language = getStringValue(LANGUAGE, "en")
        }

        ViewPump.init(
                ViewPump.builder()
                        .addInterceptor(
                                CalligraphyInterceptor(
                                        CalligraphyConfig.Builder()
                                                .setDefaultFontPath(getString(R.string.font_regular))
                                                .setFontAttrId(R.attr.fontPath)
                                                .build()
                                )
                        )
                        .build()
        )


    }


    override fun attachBaseContext(base: Context) {
        localeManager = LocaleManager(base)
        appInstance = this
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        lateinit var localeManager: LocaleManager
        private lateinit var appInstance: WooBoxApp

        //  var restApis: RestApis? = null
        var okHttpClient: OkHttpClient? = null
        var sharedPrefUtils: SharedPrefUtils? = null
        var noInternetDialog: Dialog? = null
        lateinit var language: String
        var appTheme: Int = 0

        fun getAppInstance(): WooBoxApp {
            return appInstance
        }

        fun changeAppTheme(isDark: Boolean) {
            getSharedPrefInstance().apply {
                when {
                    isDark -> setValue(Constants.SharedPref.THEME, Constants.THEME.DARK)
                    else -> setValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
                }
                appTheme = getIntValue(Constants.SharedPref.THEME, Constants.THEME.LIGHT)
            }

        }

        fun changeLanguage(aLanguage: String) {
            getSharedPrefInstance().setValue(LANGUAGE, aLanguage)
            language = aLanguage
        }


    }

    fun enableNotification(isEnabled: Boolean) {
        //OneSignal.setSubscription(isEnabled)
    }

}


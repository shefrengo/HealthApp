package com.shefrengo.health


import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.shefrengo.health.Constants.SharedPref.CONTACT
import com.shefrengo.health.Constants.SharedPref.COPYRIGHT_TEXT
import com.shefrengo.health.Constants.SharedPref.FACEBOOK
import com.shefrengo.health.Constants.SharedPref.INSTAGRAM
import com.shefrengo.health.Constants.SharedPref.LANGUAGE
import com.shefrengo.health.Constants.SharedPref.PRIVACY_POLICY
import com.shefrengo.health.Constants.SharedPref.TERM_CONDITION
import com.shefrengo.health.Constants.SharedPref.TWITTER
import com.shefrengo.health.Constants.SharedPref.WHATSAPP
import com.shefrengo.health.Constants.THEME.DARK
import com.shefrengo.health.WooBoxApp.Companion.noInternetDialog
import com.shefrengo.health.activity.BoardActivity
import com.shefrengo.health.utils.extentions.changeToolbarFont
import com.shefrengo.health.utils.extentions.getSharedPrefInstance
import com.shefrengo.health.utils.extentions.launchActivityWithNewTask
import com.shefrengo.health.utils.extentions.switchToDarkMode


import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.util.*
import kotlin.collections.ArrayList


open class AppBaseActivity : AppCompatActivity() {

    private val TAG = "AppBaseActivity"
    var db: FirebaseFirestore? = null
    private var progressDialog: Dialog? = null

    var language: Locale? = null
    private var themeApp: Int = 0
    var isAdShown = false
    var firebaseAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    fun setToolbarWithoutBackButton(mToolbar: Toolbar) {
        setSupportActionBar(mToolbar)
    }

    fun setToolbar(mToolbar: Toolbar) {
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mToolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace)
        mToolbar.changeToolbarFont()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        switchToDarkMode(WooBoxApp.appTheme == DARK)
        super.onCreate(savedInstanceState)
        noInternetDialog = null
        if (progressDialog == null) {
            progressDialog = Dialog(this)
            progressDialog?.window?.setBackgroundDrawable(ColorDrawable(0))
            progressDialog?.setContentView(R.layout.custom_dialog)
        }
        themeApp = WooBoxApp.appTheme
        language = Locale(WooBoxApp.language)
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance();
        val user = firebaseAuth!!.currentUser
        if (user != null) {
            getSharedPrefInstance().setValue(Constants.SharedPref.IS_LOGGED_IN, true)

        } else {
            getSharedPrefInstance().setValue(Constants.SharedPref.IS_LOGGED_IN, false)
        }


            // Inflate the layout for this fragment
            getSharedPrefInstance().setValue(WHATSAPP, "+260971537692")
            getSharedPrefInstance().setValue(CONTACT, "+260971537692")
            getSharedPrefInstance().setValue(PRIVACY_POLICY, "https://traders-way.flycricket.io/privacy.html")
            getSharedPrefInstance().setValue(INSTAGRAM, "http://instagram.com/ren_technology")
            getSharedPrefInstance().setValue(
                COPYRIGHT_TEXT,
                "© Ren Technology 2021 | All rights reserved"
            )
            getSharedPrefInstance().setValue(TWITTER, "http://twitter.com/BrandonRen7")
            getSharedPrefInstance().setValue(FACEBOOK, "http://facebook.com/brandon.ren.3760")
            getSharedPrefInstance().setValue(TERM_CONDITION, "")




    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun showProgress(show: Boolean) {
        when {
            show -> {
                if (!isFinishing && !progressDialog!!.isShowing) {
                    progressDialog?.setCanceledOnTouchOutside(false)
                    progressDialog?.show()
                }
            }
            else -> try {
                if (progressDialog?.isShowing!! && !isFinishing) {
                    progressDialog?.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun changeLanguage(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }

    override fun attachBaseContext(newBase: Context?) {


        super.attachBaseContext(ViewPumpContextWrapper.wrap(updateBaseContextLocale(newBase!!)))
    }

    private fun updateBaseContextLocale(context: Context): Context {

        val language = getSharedPrefInstance().getStringValue(LANGUAGE, "en")
        val locale = Locale(language)
        Locale.setDefault(locale)
        return changeLanguage(context, locale)
    }

    override fun onStart() {
        Log.d("onStart", "called")
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val locale = Locale(WooBoxApp.language)
        val appTheme = WooBoxApp.appTheme
        if (language != null && locale != language) {
            recreate()
            return
        }
        if (themeApp != 0 && themeApp != appTheme) {
            launchActivityWithNewTask<BoardActivity>()
        }

    }


}

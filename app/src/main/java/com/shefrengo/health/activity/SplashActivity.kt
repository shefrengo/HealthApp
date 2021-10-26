package com.shefrengo.health.activity

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.shefrengo.health.AppBaseActivity
import com.shefrengo.health.R
import com.shefrengo.health.utils.extentions.isLoggedIn
import com.shefrengo.health.utils.extentions.launchActivity
import com.shefrengo.health.utils.extentions.runDelayed
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class SplashActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("KeyHash:", Base64.getEncoder().encodeToString(md.digest()))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) { }
        runDelayed(1000) {

            if (isLoggedIn()){
                launchActivity<BoardActivity>()
            }else{
                launchActivity<LoginActivity>()
            }

          /**  if (getSharedPrefInstance().getBooleanValue(Constants.SharedPref.SHOW_SWIPE)) {

                /*  launchActivity<ProductDetailActivityNew> {
                      putExtra(Constants.KeyIntent.PRODUCT_ID,27)
                  }*/
            } else {

                launchActivity<WalkThroughActivity>()

            }**/
            finish()
        }
    }

}
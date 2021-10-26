package com.shefrengo.health.utils.extentions;

import com.shefrengo.health.Constants.PLAY_STORE_URL_PREFIX
import com.shefrengo.health.Constants.SharedPref.ADMIN
import com.shefrengo.health.Constants.SharedPref.DEFAULT_CURRENCY
import com.shefrengo.health.Constants.SharedPref.IS_LOGGED_IN
import com.shefrengo.health.Constants.SharedPref.KEY_ADDRESS
import com.shefrengo.health.Constants.SharedPref.KEY_DASHBOARD
import com.shefrengo.health.Constants.SharedPref.KEY_MESSAGE_COUNT
import com.shefrengo.health.Constants.SharedPref.KEY_RECENTS
import com.shefrengo.health.Constants.SharedPref.KEY_USER_ADDRESS
import com.shefrengo.health.Constants.SharedPref.NOTIFICATION_MESSAGE
import com.shefrengo.health.Constants.SharedPref.NOTIFICATION_TITLE
import com.shefrengo.health.Constants.SharedPref.USER_DISPLAY_NAME
import com.shefrengo.health.Constants.SharedPref.USER_EMAIL
import com.shefrengo.health.Constants.SharedPref.USER_FIRST_NAME
import com.shefrengo.health.Constants.SharedPref.USER_ID
import com.shefrengo.health.Constants.SharedPref.USER_IS_VENDOR
import com.shefrengo.health.Constants.SharedPref.USER_LAST_NAME
import com.shefrengo.health.Constants.SharedPref.USER_NICE_NAME
import com.shefrengo.health.Constants.SharedPref.USER_PHONE_NUMBER
import com.shefrengo.health.Constants.SharedPref.USER_PROFILE
import com.shefrengo.health.Constants.SharedPref.USER_ROLE
import com.shefrengo.health.Constants.SharedPref.USER_TOKEN
import com.shefrengo.health.Constants.SharedPref.USER_USERNAME
import com.shefrengo.health.R
import com.shefrengo.health.utils.extentions.toCamelCase


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shefrengo.health.Constants.AppBroadcasts.MyCommunitiesUPDATE
import com.shefrengo.health.Constants.AppBroadcasts.PROFILE_UPDATE
import com.shefrengo.health.Constants.SharedPref.CART_DATA
import com.shefrengo.health.Constants.SharedPref.COMMUNITY_DATA
import com.shefrengo.health.WooBoxApp
import com.shefrengo.health.WooBoxApp.Companion.getAppInstance
import com.shefrengo.health.model.Communities
import com.shefrengo.health.model.MyCommunities
import com.shefrengo.health.utils.SharedPrefUtils


/**
 * Add shared preference related to user session here
 */

fun isLoggedIn(): Boolean = getSharedPrefInstance().getBooleanValue(IS_LOGGED_IN)
fun getUserId(): String = getSharedPrefInstance().getStringValue(USER_ID)
fun getUserName(): String = getSharedPrefInstance().getStringValue(USER_USERNAME)
fun getFirstName(): String = getSharedPrefInstance().getStringValue(USER_FIRST_NAME)

fun getLastName(): String = getSharedPrefInstance().getStringValue(USER_LAST_NAME)
fun getIsVendor(): Boolean = getSharedPrefInstance().getBooleanValue(USER_IS_VENDOR)
fun getIsAdmin(): Boolean = getSharedPrefInstance().getBooleanValue(ADMIN)
fun getUserProfile(): String = getSharedPrefInstance().getStringValue(USER_PROFILE)

fun getEmail(): String = getSharedPrefInstance().getStringValue(USER_EMAIL)
fun getPhoneNumber(): String = getSharedPrefInstance().getStringValue(USER_PHONE_NUMBER)
fun getNotificationMessage(): String = getSharedPrefInstance().getStringValue(NOTIFICATION_MESSAGE)
fun getNotificationTitle(): String = getSharedPrefInstance().getStringValue(NOTIFICATION_TITLE)
fun getApiToken(): String = getSharedPrefInstance().getStringValue(USER_TOKEN)

fun getMessageCount(): String =
    getSharedPrefInstance().getLongValue(KEY_MESSAGE_COUNT, 0).toString()

fun getDefaultCurrency(): String = getSharedPrefInstance().getStringValue(DEFAULT_CURRENCY)
fun shareMyApp(context: Context, subject: String, message: String) {
    try {
        val appUrl = PLAY_STORE_URL_PREFIX + context.packageName
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, subject)
        var leadingText = "\n" + message + "\n\n"
        leadingText += appUrl + "\n\n"
        i.putExtra(Intent.EXTRA_TEXT, leadingText)
        context.startActivity(Intent.createChooser(i, "Share using"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun shareStoreLink(context: Context, subject: String, message: String, link: String) {
    try {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, subject)
        var leadingText = "\n" + message + "\n\n"
        leadingText += link + "\n\n"
        i.putExtra(Intent.EXTRA_TEXT, leadingText)
        context.startActivity(Intent.createChooser(i, "Share using"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getSharedPrefInstance(): SharedPrefUtils {

    return if (WooBoxApp.sharedPrefUtils == null) {
        WooBoxApp.sharedPrefUtils = SharedPrefUtils()
        WooBoxApp.sharedPrefUtils!!
    } else {
        WooBoxApp.sharedPrefUtils!!
    }
}

fun Context.fontMedium(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_bold))
}

fun Context.fontSemiBold(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_medium))
}

fun Context.fontBold(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.font_semibold))

}

fun ImageView.loadImageFromDrawable(@DrawableRes aPlaceHolderImage: Int) {

    Glide.with(getAppInstance()).load(aPlaceHolderImage).diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}

fun Context.openCustomTab(url: String) =
    CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))

fun Activity.getAlertDialog(
    aMsgText: String,
    aTitleText: String = getString(R.string.lbl_dialog_title),
    aPositiveText: String = getString(R.string.lbl_yes),
    aNegativeText: String = getString(R.string.lbl_no),
    onPositiveClick: (dialog: DialogInterface, Int) -> Unit,
    onNegativeClick: (dialog: DialogInterface, Int) -> Unit
): AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(aTitleText)
    builder.setMessage(aMsgText)
    builder.setPositiveButton(aPositiveText) { dialog, which ->
        onPositiveClick(dialog, which)
    }
    builder.setNegativeButton(aNegativeText) { dialog, which ->
        onNegativeClick(dialog, which)
    }
    return builder.create()
}

fun Context.getUserFullName(): String {
    return when {
        isLoggedIn() -> (getSharedPrefInstance().getStringValue(USER_FIRST_NAME) + " " + getSharedPrefInstance().getStringValue(
            USER_LAST_NAME
        )).toCamelCase()
        else -> getString(R.string.text_guest_user)
    }
}

fun clearLoginPref() {
    getSharedPrefInstance().removeKey(IS_LOGGED_IN)
    getSharedPrefInstance().removeKey(KEY_MESSAGE_COUNT)
    getSharedPrefInstance().removeKey(COMMUNITY_DATA)
    getSharedPrefInstance().removeKey(USER_ID)
    getSharedPrefInstance().removeKey(USER_DISPLAY_NAME)
    getSharedPrefInstance().removeKey(USER_EMAIL)
    getSharedPrefInstance().removeKey(USER_NICE_NAME)
    getSharedPrefInstance().removeKey(USER_TOKEN)
    getSharedPrefInstance().removeKey(USER_FIRST_NAME)
    getSharedPrefInstance().removeKey(USER_LAST_NAME)
    getSharedPrefInstance().removeKey(USER_IS_VENDOR)
    getSharedPrefInstance().removeKey(USER_PROFILE)
    getSharedPrefInstance().removeKey(USER_ROLE)
    getSharedPrefInstance().removeKey(USER_USERNAME)

    getSharedPrefInstance().removeKey(KEY_RECENTS)
    getSharedPrefInstance().removeKey(KEY_DASHBOARD)
    getSharedPrefInstance().removeKey(KEY_ADDRESS)
    getSharedPrefInstance().removeKey(KEY_USER_ADDRESS)
    getSharedPrefInstance().removeKey(ADMIN)
}

fun ImageView.loadImageFromUrl(
    aImageUrl: String,
    aPlaceHolderImage: Int = R.drawable.placeholder,
    aErrorImage: Int = R.drawable.placeholder
) {
    try {
        if (!aImageUrl.checkIsEmpty()) {
            Glide.with(getAppInstance()).load(aImageUrl).placeholder(aPlaceHolderImage)
                .error(aErrorImage).into(this)
        } else {
            loadImageFromDrawable(aPlaceHolderImage)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }


}

fun Activity.sendProfileUpdateBroadcast() {
    sendBroadcast(PROFILE_UPDATE)
}
fun Activity.sendMyCommunityUpdateBroadCast(){
    sendBroadcast(MyCommunitiesUPDATE)
}

fun Activity.sendBroadcast(action: String) {
    val intent = Intent()
    intent.action = action
    sendBroadcast(intent)
}

var firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
fun Activity.fetchAndStoreUserData() {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val userid = firebaseAuth?.uid
    val docRef = db.collection("Users").document(
        userid.toString()
    )
    docRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val name = document.getString("name")
                val surname = document.getString("surname")

                val email = document.getString("email")

                val photo = document.getString("profilePhotoUrl")
                val username = document.getString("username");
                // val phone = document.getString("phone")

                // getSharedPrefInstance().setValue(ADMIN, admin)
                getSharedPrefInstance().setValue(USER_PROFILE, photo)
                getSharedPrefInstance().setValue(USER_EMAIL, email)
                getSharedPrefInstance().setValue(USER_FIRST_NAME, name)
                getSharedPrefInstance().setValue(USER_USERNAME, username)
                getSharedPrefInstance().setValue(USER_LAST_NAME, surname)

                //     getSharedPrefInstance().setValue(USER_PHONE_NUMBER, phone)
                getSharedPrefInstance().setValue(USER_ID, userid)

                sendProfileUpdateBroadcast()

            }
        }
        .addOnFailureListener { exception ->
            Log.d("mwata", "get failed with ", exception)
        }


}

private var myCommunitiesList: ArrayList<MyCommunities> = ArrayList<MyCommunities>()
fun Activity.fetchAndStoreCommunityData() {
    val firebaseAuth = FirebaseAuth.getInstance();
    val uid = firebaseAuth.uid;

    val db = FirebaseFirestore.getInstance();
    val cartRef = db.collection("Users").document(uid.toString()).collection("MyCommunities");

    cartRef.get().addOnSuccessListener { QueryDocumentSnapshot ->

        if (!QueryDocumentSnapshot.isEmpty) {

            getSharedPrefInstance().removeKey(COMMUNITY_DATA)
            myCommunitiesList.clear()
            for (queryDocumentSnapshot in QueryDocumentSnapshot) {
                val cart = queryDocumentSnapshot.toObject(MyCommunities::class.java);
                myCommunitiesList.add(cart)
                getSharedPrefInstance().setValue(COMMUNITY_DATA, Gson().toJson(myCommunitiesList))

            }
        } else {
            getSharedPrefInstance().removeKey(COMMUNITY_DATA)
            Log.d("julo", "fetchAndStoreCommunityData: empty")

        }
    }.addOnFailureListener {
        getSharedPrefInstance().setValue(COMMUNITY_DATA, Gson().toJson(ArrayList<Communities>()))

    }


}
fun getMyCommunityListFromPref(): ArrayList<MyCommunities> {
    if (getSharedPrefInstance().getStringValue(COMMUNITY_DATA) == "") {
        return ArrayList()
    }
    return Gson().fromJson<ArrayList<MyCommunities>>(
        getSharedPrefInstance().getStringValue(COMMUNITY_DATA),
        object : TypeToken<ArrayList<MyCommunities>>() {}.type
    )
}



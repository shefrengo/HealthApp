package com.shefrengo.health.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.shefrengo.health.*
import com.shefrengo.health.Notifications.Token
import com.shefrengo.health.fragments.*
import com.shefrengo.health.utils.extentions.*

import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.bottom_bar.*
import kotlinx.android.synthetic.main.item_navigation_category.*
import kotlinx.android.synthetic.main.layout_sidebar.*
import kotlinx.android.synthetic.main.menu_cart.*
import kotlinx.android.synthetic.main.menu_profile.*
import kotlinx.android.synthetic.main.toolbar.*

class BoardActivity : AppBaseActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var selectedDashboard: Int = 0

    //region Variables
    private var count: String = ""
    private var profileCount: String = ""
    private val mHomeFragment = Home()
    private val mWishListFragment = CommunitiesFragment()

    private val mCartFragment = NotificationFragment()
    private val mProfileFragment = ProfileFragment()

    var selectedFragment: Fragment? = null

    private val TAG = "BoardActivity"
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        if (isLoggedIn()) {
            mFirebaseAnalytics!!.setUserId(getUserId());
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String> ->
                    if (!task.isSuccessful) {
                        Log.e(
                            TAG,
                            "Fetching FCM registration token failed",
                            task.exception
                        )
                        return@addOnCompleteListener
                    }
                    // Get new FCM registration token
                    val token = task.result
                    Log.d(TAG, "onCreateView: $token")
                    updateToken(token!!)
                }

        }
        if (supportFragmentManager.findFragmentById(R.id.container) != null) {
            supportFragmentManager.beginTransaction()
                .remove(supportFragmentManager.findFragmentById(R.id.container)!!).commit()
        }



        setToolbar(toolbar); setUpDrawerToggle(); loadHomeFragment(); setListener()

        BroadcastReceiverExt(this) {
            //onAction(PROFILE_UPDATE) { setUserInfo() }

        }

        tvAccount.hide()
        if (getIsAdmin()) {
            tvAdmin.show()
        } else {
            tvAdmin.hide()
        }
        if (isLoggedIn()) {
           fetchAndStoreCommunityData()
            setUserInfo();

            tvLogout.text = getString(R.string.lbl_logout)
            tvLogout.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0)


        } else {
            tvLogout.text = getString(R.string.lbl_sign_in)
            tvLogout.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_login, 0, 0, 0)
        }
        tvVersionCode.text = String.format("%S %S", "V", getAppVersionName())
    }

    override fun onResume() {
        super.onResume()
        if (selectedDashboard != getSharedPrefInstance().getIntValue(
                Constants.SharedPref.KEY_DASHBOARD,
                0
            )
        ) {
            recreate()
        }
    }


    //region Clicks
    private fun setListener() {
        civProfile.onClick {
            /**  if (isLoggedIn()) {
            launchActivity<ProfileEditActivity>()
            } else {
            launchActivity<SignUpActivity>()
            }**/
            closeDrawer()
        }
        llHome.onClick {
            closeDrawer()
            enable(ivHome)
            loadFragment(mHomeFragment)
            title = getString(R.string.home)
        }

        llWishList.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignUp>(); return@onClick
            }
            closeDrawer()
            enable(ivWishList)
            loadFragment(mWishListFragment)
            title = getString(R.string.lbl_communities)
        }
        //navigation bar wishlist
        llWishlistData.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignUp>(); return@onClick
            }
            closeDrawer()
            loadWishListFragment()
        }
        // navigation bar order

        llCart.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignUp>(); return@onClick
            }
            closeDrawer()
            enable(ivCart)
            tvNotificationCount.hide()
            loadFragment(mCartFragment)
            title = getString(R.string.notifications)
        }


        llProfile.onClick {
            if (!isLoggedIn()) {
                launchActivity<SignUp>(); return@onClick
            }

            closeDrawer()
            enable(ivProfile)
            tvMessageCount.hide()
            loadFragment(mProfileFragment)
            title = getString(R.string.profile)
        }


        tvMyCommunities.onClick {
            launchActivity<MyCommunitiesActivity>()
        }
        tvSettings.onClick {
            launchActivity<SettingsActivity>(requestCode = Constants.RequestCode.SETTINGS)
            closeDrawer()
        }

        tvAdmin.onClick {
            launchActivity<AdminPage>()
        }
        tvBlog.onClick {
            launchActivity<ChatsActivity>()

            closeDrawer()
        }
        tvRateUs.onClick {
            rateUs5Star(this@BoardActivity)
            closeDrawer()
        }
        tvShareApp.onClick {
            closeDrawer(); shareMyApp(
            this@BoardActivity,
            "",
            ""
        )
        }
        tvHelp.onClick { launchActivity<HelpActivity>(); closeDrawer() }
        tvFaq.hide()
        tvFaq.onClick { launchActivity<FAQActivity>(); closeDrawer() }
        tvContactus.onClick { launchActivity<ContactUsActivity>(); closeDrawer() }
        tvAbout.onClick { launchActivity<AboutActivity>(); closeDrawer() }
        ivCloseDrawer.onClick { closeDrawer() }


        tvLogout.onClick {
            if (isLoggedIn()) {
                val dialog = getAlertDialog(
                    getString(R.string.lbl_logout_confirmation),
                    onPositiveClick = { _, _ ->
                        firebaseAuth?.signOut()
                        clearLoginPref()

                        launchActivityWithNewTask<LoginActivity>()
                        //recreate()
                    },
                    onNegativeClick = { dialog, _ ->
                        dialog.dismiss()
                    })
                dialog.show()
                closeDrawer()
            } else {
                launchActivity<SignUp>()
            }
        }
    }
    //endregion

    //endregion

    //region Fragment Setups
    private fun loadWishListFragment() {
        enable(ivWishList)
        loadFragment(mWishListFragment)
        title = getString(R.string.lbl_wish_list)
    }



    private fun loadFragment(aFragment: Fragment) {
        if (selectedFragment != null) {
            if (selectedFragment == aFragment) return
            hideFragment(selectedFragment!!)
        }
        if (aFragment.isAdded) {
            showFragment(aFragment)
        } else {

            addFragment(aFragment, R.id.container)
        }
        selectedFragment = aFragment
    }

    fun loadHomeFragment() {
        enable(ivHome)
        //if (!mHomeFragment.isAdded) loadFragment(mHomeFragment) else showFragment(mHomeFragment)
        loadFragment(mHomeFragment)
        title = getString(R.string.home)

    }
    //endregion

    //region Common
    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(
                GravityCompat.START
            )
            !mHomeFragment.isVisible -> loadHomeFragment()
            else -> super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.RequestCode.ACCOUNT) {
            loadWishListFragment()
        }
    }

    private fun enable(aImageView: ImageView?) {
        disableAll()
        showCartCount()
        showMessageCount()
        aImageView?.background = getDrawable(R.drawable.bg_circle_primary_light)
        aImageView?.applyColorFilter(color(R.color.colorPrimary))
    }

    private fun disableAll() {
        disable(ivHome)
        disable(ivWishList)
        disable(ivCart)
        disable(ivProfile)
    }

    private fun disable(aImageView: ImageView?) {
        aImageView?.background = null
        aImageView?.applyColorFilter(color(R.color.textColorSecondary))
    }

    private fun setUpDrawerToggle() {
        val toggle = object : ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                if (WooBoxApp.language == "ar") {
                    main.translationX = -slideOffset * drawerView.width
                } else {
                    main.translationX = slideOffset * drawerView.width
                }
                (drawer_layout).bringChildToFront(drawerView)
                (drawer_layout).requestLayout()
            }
        }
        toggle.setToolbarNavigationClickListener {
            if (drawer_layout.isDrawerVisible(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                drawer_layout.openDrawer(GravityCompat.START)
            }
        }
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_drawer,
                theme
            )
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }


    private fun closeDrawer() {
        if (drawer_layout.isDrawerOpen(llLeftDrawer)) runDelayed(50) {
            drawer_layout.closeDrawer(
                llLeftDrawer
            )
        }
    }

    private fun changeProfile() {
        if (isLoggedIn()) {
            civProfile.loadImageFromUrl(
                getUserProfile(),
                aPlaceHolderImage = R.drawable.ic_profile,R.drawable.ic_profile
            )

        }
    }

    private fun setUserInfo() {
        fetchAndStoreUserData()

        txtDisplayName.text = getUserFullName()
        changeProfile()
    }


    private fun showCartCount() {
        if (isLoggedIn() && !count.checkIsEmpty() && !count.equals("0", false)) {
            tvNotificationCount.show()
        } else {
            tvNotificationCount.hide()
        }
    }

    private fun showMessageCount() {
        if (isLoggedIn() && !profileCount.checkIsEmpty() && !profileCount.equals("0", false)) {
            tvMessageCount.show()
        } else {
            tvMessageCount.hide()
        }
    }


    //endregion
    fun updateToken(token: String) {
        val userid = firebaseAuth?.uid
        val collectionReference = db!!.collection("Token")
        val token1 = Token(token)
        if (userid != null) {
            collectionReference.document(userid).set(token1, SetOptions.merge())
                .addOnFailureListener { e: java.lang.Exception? ->
                    Log.e(
                        TAG,
                        "onFailure: ",
                        e
                    )
                }
        }
    }


}
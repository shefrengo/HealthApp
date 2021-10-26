package com.shefrengo.health.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shefrengo.health.AppBaseActivity
import com.shefrengo.health.R
import com.shefrengo.health.fragments.SearchFragment
import com.shefrengo.health.utils.extentions.addFragment

class SearchActivity : AppBaseActivity() {
    private val mSearchFragment = SearchFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        addFragment(mSearchFragment, R.id.fragmentContainer)
    }
}
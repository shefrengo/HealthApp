package com.shefrengo.health.fragments

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.shefrengo.health.AppBaseActivity
import com.shefrengo.health.R
import com.shefrengo.health.utils.extentions.color


abstract class BaseFragment : Fragment(), View.OnFocusChangeListener {

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            (v as EditText).setTextColor(requireActivity().color(R.color.colorPrimaryDark))
            v.background = requireActivity().getDrawable(R.drawable.bg_ractangle_rounded_active)
        } else {
            (v as EditText).setTextColor(requireActivity().color(R.color.textColorPrimary))
            v.background = requireActivity().getDrawable(R.drawable.bg_ractangle_rounded_inactive)
        }

    }

    fun hideProgress() {

        if (activity !=null && isAdded)
            (requireActivity() as AppBaseActivity).showProgress(false)
    }

    fun showProgress() {

        if (activity !=null && isAdded)
            (requireActivity() as AppBaseActivity).showProgress(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    object biggerDotTranformation : PasswordTransformationMethod() {

        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return PasswordCharSequence(super.getTransformation(source, view))
        }

        private class PasswordCharSequence(val transformation: CharSequence) : CharSequence by transformation {
            override fun get(index: Int): Char = if (transformation[index] == DOT) {
                BIGGER_DOT
            } else {
                transformation[index]
            }
        }

        private const val DOT = '\u2022'
        private const val BIGGER_DOT = '●'
    }
}
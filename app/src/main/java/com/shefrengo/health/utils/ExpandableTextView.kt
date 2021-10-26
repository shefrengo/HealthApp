package com.shefrengo.health.utils;

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView

import androidx.annotation.RequiresApi
import com.shefrengo.health.R
import com.shefrengo.health.utils.extentions.hide
import com.shefrengo.health.utils.extentions.show


import java.util.Random

class ExpandableTextView : LinearLayout {

    private var mMainLayout: View? = null
    var content: TextView? = null
    var moreLess: TextView? = null
    var maxLine = Integer.MAX_VALUE
    private var collapsedHeight: Int = 0
    private var expandInterpolator: TimeInterpolator? = null
    private var collapseInterpolator: TimeInterpolator? = null
    /**
     * Sets the duration of the expand / collapse animation.
     *
     * @param animationDuration duration in milliseconds.
     */
    var animationDuration: Long = 300

    var moreLessGravity: Int = 0
        set(moreLessGravity) {
            var i = Gravity.LEFT
            field = moreLessGravity
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            if (moreLessGravity != Gravity.LEFT) {
                i = Gravity.RIGHT
            }
            params.gravity = i
            moreLess!!.layoutParams = params
        }
    /**
     * Options
     */
    var isMoreLessShow: Boolean = false
        set(moreLessShow) {
            field = moreLessShow
            if (moreLessShow) {
                moreLess!!.show()
            } else {
                moreLess!!.hide()
            }
        }
    private var moreLessTextStyle: Int = 0
    private var contentTextStyle: Int = 0
    var isDefaultExpand: Boolean = false
        private set
    private var mContext: Context? = null


    private fun getID(): Int {
        val generator = Random()
        return generator.nextInt(Integer.MAX_VALUE)

    }

    constructor(context: Context) : super(context) {
        this.mContext = context
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        init(attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.mContext = context
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        mMainLayout = View.inflate(context, R.layout.expandable_text_view_layout, this)
        content = findViewById<View>(R.id.content) as TextView
        moreLess = findViewById<View>(R.id.moreLess) as TextView
        content!!.setOnClickListener { toggle() }
        moreLess!!.setOnClickListener { toggle() }
        content!!.id = getID()
        // create default interpolators
        expandInterpolator = AccelerateDecelerateInterpolator()
        collapseInterpolator = AccelerateDecelerateInterpolator()
        applyXmlAttributes(attrs)
    }

    private fun applyXmlAttributes(attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
            try {
                // maxLine = a.getInt(R.styleable.ExpandableTextView_hnc_maxLine, maxLine);
                maxLine = 3
                isDefaultExpand = true
                content!!.text = a.getString(R.styleable.ExpandableTextView_hnc_text)
                if (isDefaultExpand) {
                    content!!.maxLines = Integer.MAX_VALUE
                    moreLess!!.text = resources.getString(R.string.more)
                } else {
                    content!!.maxLines = maxLine
                    moreLess!!.text = resources.getString(R.string.less)
                }
                //content.setTextColor(a.getColor(R.styleable.ExpandableTextView_hnc_textColor, Color.BLACK));
                // content.setTextSize(0, (float) a.getDimensionPixelSize(R.styleable.ExpandableTextView_hnc_textSize, 20));
                moreLess!!.isAllCaps = a.getBoolean(R.styleable.ExpandableTextView_hnc_moreLessAllCaps, true)
                // moreLess.setTextColor(a.getColor(R.styleable.ExpandableTextView_hnc_moreLessTextColor, Color.BLACK));
                // moreLess.setTextSize(0, (float) a.getDimensionPixelSize(R.styleable.ExpandableTextView_hnc_moreLessTextSize, 20));
                isMoreLessShow = true
                moreLessGravity = a.getInt(R.styleable.ExpandableTextView_hnc_moreLessGravity, Gravity.LEFT)
                moreLessTextStyle = a.getInt(R.styleable.ExpandableTextView_hnc_moreLessTextStyle, Typeface.NORMAL)
                applyMoreLessStyle()
                contentTextStyle = a.getInt(R.styleable.ExpandableTextView_hnc_TextStyle, Typeface.NORMAL)
                applyStyle()
                animationDuration = a.getInt(R.styleable.ExpandableTextView_hnc_animationDuration, 300).toLong()
            } finally {
                a.recycle()
            }
        }
    }

    fun toggle() {
        if (moreLess!!.text == resources.getString(R.string.more)) {
            toggle(true)
        } else {
            toggle(false)
        }
    }

    private fun toggle(expand: Boolean) {
        if (expand) {
            expand()
            moreLess!!.text = resources.getString(R.string.less)
            return
        }
        collapse()
        moreLess!!.text = resources.getString(R.string.more)
    }

    fun setText(text: String) {
        content!!.maxLines = 5
        content!!.text = text
        val vto = content!!.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {
                val obs = content!!.viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (content!!.lineCount <= maxLine) {
                    moreLess!!.hide()
                } else {
                    moreLess!!.show()
                    content!!.maxLines = maxLine
                }
            }
        })
    }

    /**
     * Expand this [TextView].
     *
     * @return true if expanded, false otherwise.
     */
    fun expand() {

        // get collapsed height
        content!!.measure(
                View.MeasureSpec.makeMeasureSpec(content!!.measuredWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        collapsedHeight = content!!.measuredHeight

        // set maxLines to MAX Integer, so we can calculate the expanded height
        content!!.maxLines = Integer.MAX_VALUE

        // get expanded height
        content!!.measure(
                View.MeasureSpec.makeMeasureSpec(content!!.measuredWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val expandedHeight = content!!.measuredHeight

        // animate from collapsed height to expanded height
        val valueAnimator = ValueAnimator.ofInt(collapsedHeight, expandedHeight)
        valueAnimator.addUpdateListener { animation ->
            val layoutParams = content!!.layoutParams
            layoutParams.height = animation.animatedValue as Int
            content!!.layoutParams = layoutParams
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // if fully expanded, set height to WRAP_CONTENT, because when rotating the device
                // the height calculated with this ValueAnimator isn't correct anymore
                val layoutParams = content!!.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                content!!.layoutParams = layoutParams
            }
        })

        // set interpolator
        valueAnimator.interpolator = expandInterpolator

        // start the animation
        valueAnimator
                .setDuration(animationDuration)
                .start()

    }

    /**
     * Collapse this [TextView].
     *
     * @return true if collapsed, false otherwise.
     */
    fun collapse() {
        // get expanded height
        val expandedHeight = content!!.measuredHeight

        // animate from expanded height to collapsed height
        val valueAnimator = ValueAnimator.ofInt(expandedHeight, collapsedHeight)
        valueAnimator.addUpdateListener { animation ->
            val layoutParams = content!!.layoutParams
            layoutParams.height = animation.animatedValue as Int
            content!!.layoutParams = layoutParams
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // set maxLines to original value
                content!!.maxLines = maxLine

                // if fully collapsed, set height to WRAP_CONTENT, because when rotating the device
                // the height calculated with this ValueAnimator isn't correct anymore
                val layoutParams = content!!.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                content!!.layoutParams = layoutParams
            }
        })

        // set interpolator
        valueAnimator.interpolator = collapseInterpolator

        // start the animation
        valueAnimator
                .setDuration(animationDuration)
                .start()

    }

    /**
     * Sets a [TimeInterpolator] for expanding and collapsing.
     *
     * @param interpolator the interpolator
     */
    fun setInterpolator(interpolator: TimeInterpolator) {
        expandInterpolator = interpolator
        collapseInterpolator = interpolator
    }

    /**
     * Sets a [TimeInterpolator] for expanding.
     *
     * @param expandInterpolator the interpolator
     */
    fun setExpandInterpolator(expandInterpolator: TimeInterpolator) {
        this.expandInterpolator = expandInterpolator
    }

    /**
     * Sets a [TimeInterpolator] for collpasing.
     *
     * @param collapseInterpolator the interpolator
     */
    fun setCollapseInterpolator(collapseInterpolator: TimeInterpolator) {
        this.collapseInterpolator = collapseInterpolator
    }

    fun getMoreLessTextStyle(): Int {
        return moreLessTextStyle
    }

    fun setMoreLessTextStyle(textStyle: Int) {
        moreLessTextStyle = textStyle
        applyMoreLessStyle()
    }

    private fun applyMoreLessStyle() {
        setTypeface(moreLess, moreLessTextStyle)
    }

    private fun setTypeface(textView: TextView?, testStyle: Int) {
        /* switch (testStyle) {
            case 1:
                textView.setTypeface( Typeface.createFromAsset(mContext.getAssets(), "googlesansregular.ttf"));
                break;
            case 2:
                textView.setTypeface( Typeface.createFromAsset(mContext.getAssets(), "googlesansmedium.ttf"));
                break;
            case 3:
                textView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "googlesansbold.ttf"));
                break;
        }*/

    }

    private fun applyStyle() {
        setTypeface(content, contentTextStyle)

    }

    fun getContentTextStyle(): Int {
        return contentTextStyle
    }

    fun setContentTextStyle(contentTextStyle: Int) {
        this.contentTextStyle = contentTextStyle
        applyStyle()
    }

    fun setTextColor(color: Int) {
        content!!.setTextColor(color)
    }

    fun setMoreLessColor(color: Int) {
        moreLess!!.setTextColor(color)
    }

    fun setMoreLessTextSize(size: Int) {
        moreLess!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
    }

    fun setTextSize(size: Int) {
        content!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, size.toFloat())
    }

    fun setMoreLessAllCaps(allCaps: Boolean) {
        moreLess!!.isAllCaps = allCaps
    }

    fun setContentClick(listener: View.OnClickListener?) {
        if (listener != null) {
            content!!.setOnClickListener(listener)
        }
    }
}
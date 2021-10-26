package com.shefrengo.health.utils;

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.shefrengo.health.R


class LineView : View {
    private var mLinePaint: Paint? = null
    private var mLineColor: Int = 0


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLine(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLine(context, attrs)
    }

    constructor(context: Context) : super(context) {

    }

    private fun initLine(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LineView, 0, 0)
        try {
            mLineColor = ta.getColor(R.styleable.LineView_line_color, DEFAULT_TEXT_COLOR)
            mLinePaint = Paint()
            mLinePaint!!.isAntiAlias = true
            mLinePaint!!.color = mLineColor
            mLinePaint!!.style = Paint.Style.STROKE
            mLinePaint!!.strokeWidth = 5f
            mLinePaint!!.pathEffect = DashPathEffect(floatArrayOf(10.toFloat(), 5.toFloat()), 0.0f)
        } finally {
            ta.recycle()
        }
    }

    fun setLineColor(mLineColor: Int) {
        this.mLineColor = mLineColor
        mLinePaint!!.color = mLineColor

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(
                (width / 2).toFloat(),
                0f, // startY
                (width / 2).toFloat(), // stopX
                (canvas.height - 0).toFloat(), // stopY
                mLinePaint!! // Paint
        )
    }

    companion object {
        private val DEFAULT_TEXT_COLOR = Color.GREEN
    }
}
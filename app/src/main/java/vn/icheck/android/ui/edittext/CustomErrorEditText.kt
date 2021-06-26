package vn.icheck.android.ui.edittext

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.util.ick.toPx
import vn.icheck.android.ichecklibs.util.dpToPx

open class CustomErrorEditText : AppCompatEditText {

    var mErrorDrawable: Drawable? = null
    var mError: CharSequence? = null
    lateinit var mErrorPaint: Paint
    lateinit var mErrorTextPaint: Paint
    lateinit var mLinePaint: Paint

    constructor(context: Context) : super(context) {
        initEdt(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initEdt(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        initEdt(context)
    }

    private fun initEdt(context: Context) {
        mErrorDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_error_red_18dp, null)
        mErrorPaint = Paint()
        mErrorTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mErrorTextPaint.textSize = 12 * getResources().getDisplayMetrics().scaledDensity
        mErrorTextPaint.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        mLinePaint = Paint()
        mLinePaint.strokeWidth = 1f.toPx()

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + 20.dpToPx())
        mErrorTextPaint.color = ColorManager.getAccentRedColor(context)
        setBackgroundResource(0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            val bottom = height - paddingBottom + 2.5f.toPx()
            if (!mError.isNullOrEmpty()) {
                mLinePaint.setColor(ColorManager.getAccentRedColor(context))
                drawLine(0f + paddingStart, bottom.toFloat(), (width - paddingStart - paddingEnd).toFloat(), bottom.toFloat(), mLinePaint)
                drawBitmap(
                        mErrorDrawable!!.toBitmap(),
                        0f,
                        bottom.toFloat(),
                        mErrorPaint
                )
                drawText(
                        mError.toString(),
                        26.dpToPx().toFloat(),
                        (bottom + 16f.toPx()).toFloat(),
                        mErrorTextPaint
                )
            } else {
                if (hasFocus()) {
                    mLinePaint.setColor(ColorManager.getPrimaryColor(context))
                } else {
                    mLinePaint.setColor(Color.parseColor("#D8D8D8"))
                }
                drawLine(0f + paddingStart, bottom.toFloat(), (width - paddingStart - paddingEnd).toFloat(), bottom.toFloat(), mLinePaint)
            }
        }
    }

    override fun onTextChanged(
            text: CharSequence?,
            start: Int,
            lengthBefore: Int,
            lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        mError = null
    }


    override fun setError(error: CharSequence?) {
        mError = error
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        mError = error
    }
}
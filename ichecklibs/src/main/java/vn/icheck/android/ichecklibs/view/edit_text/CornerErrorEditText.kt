package vn.icheck.android.ichecklibs.view.edit_text

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.util.dpToPx

open class CornerErrorEditText : AppCompatEditText {
    var originalPadding = 0
    lateinit var mErrorTextPaint: Paint
    lateinit var mBackgroundPaint: Paint
    var mError: CharSequence? = null
    lateinit var rect: RectF
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    fun initView() {
        //            setBackgroundResource(R.drawable.gray_stroke_corner_4)
        mErrorTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mErrorTextPaint.textSize = 12 * getResources().getDisplayMetrics().scaledDensity
        mErrorTextPaint.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        mErrorTextPaint.color = ContextCompat.getColor(context, R.color.colorAccentRed)
        originalPadding = paddingBottom
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint.strokeWidth = 1f.dpToPx()
        mBackgroundPaint.style = Paint.Style.STROKE
        mBackgroundPaint.setColor(Color.parseColor("#D8D8D8"))
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
        setBackgroundResource(0)

    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (!text.isNullOrEmpty() && originalPadding != paddingBottom) {
//            setBackgroundResource(R.drawable.gray_stroke_corner_4)
            mBackgroundPaint.setColor(Color.parseColor("#D8D8D8"))
            setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
            requestLayout()
        }
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            canvas.translate(scrollX.toFloat(), 0f)
            val bottom = height - paddingBottom + 2.5f.dpToPx()
            if (!::rect.isInitialized) {
                rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            }
            drawRoundRect(rect, 4f.dpToPx(), 4f.dpToPx(), mBackgroundPaint)
            if (!mError.isNullOrEmpty()) {
                drawText(
                        mError.toString(),
                        0f,
                        (bottom + 23.5f.dpToPx()).toFloat(),
                        mErrorTextPaint
                )
            }
            canvas.translate(0f, 0f)
        }
    }

    override fun setError(error: CharSequence?) {
        mError = error
//        setBackgroundResource(R.drawable.red_stroke_corner_4)
        mBackgroundPaint.setColor(ContextCompat.getColor(context, R.color.colorAccentRed))
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
        requestLayout()
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + 20.dpToPx())
        requestLayout()
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        mError = error
//        setBackgroundResource(R.drawable.red_stroke_corner_4)
        mBackgroundPaint.setColor(ContextCompat.getColor(context, R.color.colorAccentRed))
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
        requestLayout()
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + 20.dpToPx())
        requestLayout()
    }

}
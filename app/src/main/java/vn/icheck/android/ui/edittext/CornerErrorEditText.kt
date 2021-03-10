package vn.icheck.android.ui.edittext

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import vn.icheck.android.R
import vn.icheck.android.util.ick.toPx

class CornerErrorEditText:AppCompatEditText {
    var originalPadding = 0
    lateinit var mErrorTextPaint: Paint
    lateinit var mBackgroundPaint: Paint
    var mError: CharSequence? = null
    lateinit var rect:RectF
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
        mErrorTextPaint.color = Color.parseColor("#FF0000")
        originalPadding = paddingBottom
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint.strokeWidth = 1f.toPx()
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
            val bottom = height - paddingBottom + 2.5f.toPx()
            if (!::rect.isInitialized) {
                rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            }
            drawRoundRect(rect, 4f.toPx(), 4f.toPx(), mBackgroundPaint)
            if (!mError.isNullOrEmpty()) {
                drawText(
                        mError.toString(),
                        0f,
                        (bottom + 23.5f.toPx()).toFloat(),
                        mErrorTextPaint
                )
            }
            canvas.translate(0f, 0f)
        }
    }

    override fun setError(error: CharSequence?) {
        mError = error
//        setBackgroundResource(R.drawable.red_stroke_corner_4)
        mBackgroundPaint.setColor(Color.parseColor("#FF0000"))
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
        requestLayout()
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + 20.toPx())
        requestLayout()
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        mError = error
//        setBackgroundResource(R.drawable.red_stroke_corner_4)
        mBackgroundPaint.setColor(Color.parseColor("#FF0000"))
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
        requestLayout()
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + 20.toPx())
        requestLayout()
    }

}
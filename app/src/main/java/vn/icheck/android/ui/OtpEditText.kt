package vn.icheck.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class OtpEditText:AppCompatEditText {

    private var mSpace = 10f
    private var mNumChar = 6
    private var mLineSpacing = 8f
    private var mMaxLength = 6
    private var mLineStroke = 2f
    private lateinit var mLinePaint: Paint
    private lateinit var mRect:Rect

    constructor(context:Context):super(context)
    constructor(context: Context, attrs:AttributeSet):super(context, attrs){
        initView(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle:Int):super(context, attrs, defStyle){
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet) {
        mRect = Rect()
        val multi = context.resources.displayMetrics.density
        mLineStroke *= multi
        mLinePaint = Paint(paint)
        mLinePaint.strokeWidth = mLineStroke
        mLinePaint.color = vn.icheck.android.ichecklibs.Constant.getLineColor(context)
        setBackgroundResource(0)
        mSpace *= multi
        mLineSpacing *= multi
        super.setOnClickListener {
            setSelection(text?.length!!)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val availableWidth = width - paddingRight - paddingLeft
        val mCharSize:Float = if (mSpace < 0) {
            (availableWidth / (mNumChar * 2 - 1)).toFloat()
        } else {
            (availableWidth - (mSpace * (mNumChar  - 1)))/ mNumChar
        }
        var startX:Float = paddingLeft.toFloat()
        val bottom = height - paddingBottom
        val textLength = text?.length
        val textWidth = FloatArray(textLength!!)
        paint.getTextWidths(text, 0, text?.length!!, textWidth)
        for (i in 0 until mNumChar) {
            if (i + 1 <= textLength) {
                mLinePaint.color = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(context)
            } else {
                mLinePaint.color = vn.icheck.android.ichecklibs.Constant.getLineColor(context)
            }
            canvas?.drawLine(startX, bottom.toFloat(), startX + mCharSize, bottom.toFloat(), mLinePaint)
            if (text!!.length > i) {
                val middle = startX + mCharSize / 2
                canvas?.drawText(text!!, i, i + 1, middle - textWidth[0] / 2, bottom - mLineSpacing, paint)
            }
            startX += if (mSpace < 0) {
                mCharSize * 2
            } else {
                mCharSize + mSpace
            }
        }

    }
}
package vn.icheck.android.ichecklibs.view

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import vn.icheck.android.ichecklibs.R
import kotlin.math.roundToInt

class UserNameText:LinearLayout {

    constructor(context: Context?) : super(context){
        initView()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    private lateinit var firstLine:TextView
    private lateinit var secondLine:TextView
    var textWidth = 0f
    var maxCharPerLine = 1
    var isVerified = false
    var text:CharSequence = ""

    fun initView() {
        val mp = Paint()
        val rect = Rect()
        mp.setTypeface( Typeface.createFromAsset(resources.assets, "font/barlow_medium.ttf"))
        mp.textSize = resources.getDimension(R.dimen.font_12)
        firstLine = TextView(context).apply {
            maxLines = 1
            typeface = Typeface.createFromAsset(resources.assets, "font/barlow_medium.ttf")
            textSize = resources.getDimension(R.dimen.font_12)
            includeFontPadding = false

        }
        secondLine = TextView(context).apply {
            maxLines = 1
            typeface = Typeface.createFromAsset(resources.assets, "font/barlow_medium.ttf")
            textSize = resources.getDimension(R.dimen.font_12)
            ellipsize = TextUtils.TruncateAt.END
            includeFontPadding = false

        }
        val text = "H"
        mp.getTextBounds(text, 0, text.length, rect)
//        if (rect.width() > 0) {
//            textWidth = rect.width().toFloat() + 1f
//        } else {
//            textWidth = 10f
//        }
        textWidth = mp.measureText(text,0,1)

        addView(firstLine)
        addView(secondLine)
        orientation = VERTICAL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maxCharPerLine = (layoutParams.width / textWidth).roundToInt()
        if (maxCharPerLine > 0) {
            setText(text, isVerified)
        }
    }

    fun setText(charSequence: CharSequence, verified:Boolean = false) {

        isVerified = verified
        text = charSequence
        if (maxCharPerLine > 0) {
            val lp = firstLine.layoutParams as LinearLayout.LayoutParams
            lp.width = LayoutParams.MATCH_PARENT
            firstLine.layoutParams = lp

            val lp2 = secondLine.layoutParams as LinearLayout.LayoutParams
            lp2.width = LayoutParams.WRAP_CONTENT
            secondLine.layoutParams = lp2

            val totalText = charSequence.length
            if (totalText > maxCharPerLine) {
                firstLine.setText(charSequence.subSequence(0,maxCharPerLine - 1))
                if (verified) {
                    secondLine.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px,0)
                }
                secondLine.setText(charSequence.subSequence(maxCharPerLine - 1, charSequence.lastIndex))
            } else {
                if (verified) {
                    firstLine.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_verified_16px,0)
                }
                firstLine.setText(charSequence)
            }
        }
    }
}
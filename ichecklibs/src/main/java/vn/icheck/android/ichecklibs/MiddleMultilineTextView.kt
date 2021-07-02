package vn.icheck.android.ichecklibs

import android.content.Context
import android.graphics.Canvas
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import kotlin.math.ceil

open class MiddleMultilineTextView : AppCompatTextView {
    private val SYMBOL = " ... "
    private val SYMBOL_LENGTH = SYMBOL.length

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (maxLines > 1) {
            val originalLength = text.length
            val visibleLength = visibleLength
            if (originalLength > visibleLength) {
                setSpannableString(smartTrim(text.toString().trim(), visibleLength - SYMBOL_LENGTH), icon)
            }
        }
    }

    private fun smartTrim(string: String?, maxLength: Int): String? {
        return if (string == null) null
        else if (string.length <= maxLength) string
        else {
            val leftVisible = ceil((maxLength / 2).toDouble()).toInt()
            val rightVisible = maxLength - leftVisible
            string.substring(0, leftVisible - 1) + SYMBOL + string.substring(string.length - rightVisible + 1)
        }
    }

    fun setSpannableString(string: String?, icon: Int) {
        text = if (icon != -1) {
            val spannableString = SpannableString("$string  ")
            spannableString.setSpan(imageSpan, (string ?: "").length + 1, (string ?: "").length + 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableString
        } else {
            string
        }
    }

    var icon: Int = -1
    var originText: String? = ""
    var imageSpan: ImageSpan? = null

    fun setDrawableNextEndText(text: String?, icon: Int) {
        this.icon = icon
        this.originText = text
        val drawable = ContextCompat.getDrawable(context, icon)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        imageSpan = ImageSpan(drawable!!, ImageSpan.ALIGN_BASELINE)
        setSpannableString(originText, icon)
    }

    private val visibleLength: Int
        private get() {
            val start = layout.getLineStart(0)
            val end = layout.getLineEnd(maxLines - 1)
            return text.toString().substring(start, end).length
        }
}
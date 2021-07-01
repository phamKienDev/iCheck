package vn.icheck.android.ichecklibs

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

open class MiddleMultilineTextView : AppCompatTextView {
    private val SYMBOL = " ... "
    private val SYMBOL_LENGTH = SYMBOL.length

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (maxLines > 1) {
            val originalLength = text.length
            val visibleLength = visibleLength
            if (originalLength > visibleLength) {
                text = smartTrim(text.toString(), visibleLength - SYMBOL_LENGTH)
            }
        }
    }

    private fun smartTrim(string: String?, maxLength: Int): String? {
        if (string == null) return null
        if (maxLength < 1) return string
        if (string.length <= maxLength) return string
        if (maxLength == 1) return string.substring(0, 1) + "..."
        val midpoint = Math.ceil((string.length / 2).toDouble()).toInt()
        val toremove = string.length - maxLength
        val lstrip = Math.ceil((toremove / 2).toDouble()).toInt()
        val rstrip = toremove - lstrip
        var subTo = midpoint - lstrip - 4
        if (subTo < 0) {
            subTo = 0
        }
        return string.substring(0, subTo) + SYMBOL + string.substring(midpoint + rstrip)
    }

    private val visibleLength: Int
        private get() {
            val start = layout.getLineStart(0)
            val end = layout.getLineEnd(maxLines - 1)
            return text.toString().substring(start, end).length
        }
}
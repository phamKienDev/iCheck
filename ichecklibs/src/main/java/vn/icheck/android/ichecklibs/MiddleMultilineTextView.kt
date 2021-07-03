package vn.icheck.android.ichecklibs

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.math.ceil

open class MiddleMultilineTextView : AppCompatTextView {
    private val SYMBOL = " ... "
    private val SYMBOL_LENGTH = SYMBOL.length
    private var icon: Int = -1
    private var imageSpan: ImageSpan? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.Default) {
            if (maxLines > 1) {
                val originalLength = text.length
                val visibleLength = getVisibleLength(text.toString())
                if (originalLength > visibleLength) {
                    val str = smartTrim(text.toString().trim(), visibleLength - SYMBOL_LENGTH)
                    withContext(Dispatchers.Main) {
                        setSpannableString(str, icon)
                    }
                }
            }
        }
    }

    fun setDrawableNextEndText(text: String?, icon: Int) {
        this.icon = icon
        val drawable = ContextCompat.getDrawable(context, icon)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        imageSpan = ImageSpan(drawable!!, ImageSpan.ALIGN_BASELINE)
        setSpannableString(text, icon)
    }

    private fun setSpannableString(string: String?, icon: Int) {
        text = if (icon != -1) {
            val spannableString = SpannableString("$string  ")
            spannableString.setSpan(imageSpan, (string ?: "").length + 1, (string ?: "").length + 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableString
        } else {
            string
        }
    }

    private suspend fun getVisibleLength(text: String): Int {
        return coroutineScope {
            val output = async(Dispatchers.Default) {
                val start = layout.getLineStart(0)
                val end = layout.getLineEnd(maxLines - 1)
                text.substring(start, end).length
            }
            output.await()
        }
    }

    private suspend fun smartTrim(string: String?, maxLength: Int): String? {
        return coroutineScope {
            val output = async(Dispatchers.Default) {
                when {
                    string == null -> null
                    string.length <= maxLength -> string
                    else -> {
                        val leftVisible = ceil((maxLength / 2).toDouble()).toInt()
                        val rightVisible = maxLength - leftVisible
                        string.substring(0, leftVisible - 1) + SYMBOL + string.substring(string.length - rightVisible + 1)
                    }
                }
            }
            output.await()
        }
    }
}
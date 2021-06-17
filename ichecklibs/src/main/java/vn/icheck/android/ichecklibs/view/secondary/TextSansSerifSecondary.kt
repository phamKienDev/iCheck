package vn.icheck.android.ichecklibs.view.secondary

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R

class TextSansSerifSecondary : AppCompatTextView {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setTextColor(Constant.getSecondaryColor(context))
        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        includeFontPadding = false
    }
}
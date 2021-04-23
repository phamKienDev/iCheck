package vn.icheck.android.ichecklibs.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R

class TextSubheader2Primary : AppCompatTextView {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setTextColor(Constant.getPrimaryColor(context))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        typeface = ResourcesCompat.getFont(context, R.font.barlow_semi_bold)
        includeFontPadding = false
    }
}
package vn.icheck.android.ichecklibs.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import vn.icheck.android.ichecklibs.R

open class EditTextTitle : AppCompatTextView {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        typeface = ResourcesCompat.getFont(context, R.font.barlow_medium)
        includeFontPadding = false
    }
}
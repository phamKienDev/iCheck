package vn.icheck.android.ichecklibs.view.secondary

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ichecklibs.Constant

open class ButtonSecondary : AppCompatButton {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setTextColor(Constant.getSecondaryColor(context))
        includeFontPadding = false
    }
}
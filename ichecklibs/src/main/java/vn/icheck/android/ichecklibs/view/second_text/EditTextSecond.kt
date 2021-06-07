package vn.icheck.android.ichecklibs.view.second_text

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import vn.icheck.android.ichecklibs.Constant

open class EditTextSecond : AppCompatEditText {
    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    private fun setup() {
        setTextColor(Constant.getNormalTextColor(context))
        includeFontPadding=false
    }
}
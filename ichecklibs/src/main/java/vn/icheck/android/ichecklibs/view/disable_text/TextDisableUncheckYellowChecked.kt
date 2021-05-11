package vn.icheck.android.ichecklibs.view.disable_text

import android.content.Context
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.ViewHelper

class TextDisableUncheckYellowChecked  : AppCompatCheckedTextView {

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setup()
    }

    private fun setup() {
        setTextColor(ViewHelper.createColorStateList(Constant.getDisableTextColor(context),ContextCompat.getColor(context, R.color.colorAccentYellow)))
        includeFontPadding = false
    }
}
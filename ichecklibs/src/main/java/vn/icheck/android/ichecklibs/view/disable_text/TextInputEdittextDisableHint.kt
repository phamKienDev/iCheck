package vn.icheck.android.ichecklibs.view.disable_text

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.ViewHelper

open class TextInputEdittextDisableHint :TextInputEditText{
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
        setHintTextColor(ColorManager.getDisableTextColor(context))
        includeFontPadding = false
    }
}
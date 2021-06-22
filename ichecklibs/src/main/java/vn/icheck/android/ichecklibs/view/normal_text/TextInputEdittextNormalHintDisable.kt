package vn.icheck.android.ichecklibs.view.normal_text

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import vn.icheck.android.ichecklibs.ColorManager

open class TextInputEdittextNormalHintDisable :TextInputEditText{
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
        setTextColor(ColorManager.getNormalTextColor(context))
        includeFontPadding = false
    }
}
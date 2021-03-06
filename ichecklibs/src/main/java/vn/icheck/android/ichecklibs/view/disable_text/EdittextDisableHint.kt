package vn.icheck.android.ichecklibs.view.disable_text

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import vn.icheck.android.ichecklibs.ColorManager

open class EdittextDisableHint : AppCompatEditText {

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

    }
}
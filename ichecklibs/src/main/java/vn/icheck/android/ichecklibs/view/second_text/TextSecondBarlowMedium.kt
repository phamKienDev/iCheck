package vn.icheck.android.ichecklibs.view.second_text

import android.content.Context
import android.util.AttributeSet
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.view.TextBarlowMedium

class TextSecondBarlowMedium : TextBarlowMedium {
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
        setTextColor(ColorManager.getSecondTextColor(context))
    }
}
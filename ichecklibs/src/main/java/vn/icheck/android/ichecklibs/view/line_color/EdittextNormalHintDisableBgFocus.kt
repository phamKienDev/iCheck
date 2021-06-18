package vn.icheck.android.ichecklibs.view.line_color

import android.content.Context
import android.util.AttributeSet
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.view.normal_text.EdittextNormalHintDisable

class EdittextNormalHintDisableBgFocus : EdittextNormalHintDisable {
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
        background = ViewHelper.bgEdtSelectorFocusLineColor1(context)
        setPadding(SizeHelper.size5,SizeHelper.size5,SizeHelper.size5,SizeHelper.size5)
    }
}
package vn.icheck.android.ichecklibs.view.primary

import android.content.Context
import android.util.AttributeSet
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.view.TextBody2
import vn.icheck.android.ichecklibs.view.TextSubheader2

class TextBody2Primary : TextBody2 {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setTextColor(Constant.getPrimaryColor(context))
    }
}
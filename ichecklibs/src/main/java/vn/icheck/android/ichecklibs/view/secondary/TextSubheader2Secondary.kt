package vn.icheck.android.ichecklibs.view.secondary

import android.content.Context
import android.util.AttributeSet
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.view.TextSubheader2

class TextSubheader2Secondary : TextSubheader2 {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setTextColor(ColorManager.getSecondaryColor(context))
    }
}
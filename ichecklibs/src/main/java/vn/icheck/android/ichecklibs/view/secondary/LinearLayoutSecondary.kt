package vn.icheck.android.ichecklibs.view.secondary

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant

class LinearLayoutSecondary : LinearLayout {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setBackgroundColor(ColorManager.getSecondaryColor(context))
    }
}
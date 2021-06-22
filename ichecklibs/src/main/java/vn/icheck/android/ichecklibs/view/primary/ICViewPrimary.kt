package vn.icheck.android.ichecklibs.view.primary

import android.content.Context
import android.util.AttributeSet
import android.view.View
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant

class ICViewPrimary : View {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setBackgroundColor(ColorManager.getPrimaryColor(context))
    }
}
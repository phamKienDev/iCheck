package vn.icheck.android.ichecklibs.view.accent.accentblue

import android.content.Context
import android.util.AttributeSet
import com.rd.PageIndicatorView
import vn.icheck.android.ichecklibs.Constant

class PageIndicatorSelectAccentBlue : PageIndicatorView {
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
        selectedColor = Constant.getAccentBlueColor(context)
        unselectedColor = Constant.getAccentBlueColor(context)
    }
}
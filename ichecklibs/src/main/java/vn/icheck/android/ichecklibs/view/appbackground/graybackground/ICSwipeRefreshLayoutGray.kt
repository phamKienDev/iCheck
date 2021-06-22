package vn.icheck.android.ichecklibs.view.appbackground.graybackground

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import vn.icheck.android.ichecklibs.ColorManager

class ICSwipeRefreshLayoutGray : SwipeRefreshLayout {
    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }


    private fun setup() {
        setBackgroundColor(ColorManager.getAppBackgroundGrayColor(context))
    }
}
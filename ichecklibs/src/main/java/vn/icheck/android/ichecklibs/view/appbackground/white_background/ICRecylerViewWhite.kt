package vn.icheck.android.ichecklibs.view.appbackground.white_background

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ichecklibs.Constant

class ICRecylerViewWhite : RecyclerView {
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
        setBackgroundColor(Constant.getAppBackgroundWhiteColor(context))
    }
}

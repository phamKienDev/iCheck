package vn.icheck.android.ichecklibs.view.appbackground.graybackground

import android.content.Context
import android.util.AttributeSet
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.ichecklibs.Constant

class ICCircleImageViewGray:CircleImageView {
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
        circleBackgroundColor=Constant.getAppBackgroundGrayColor(context)
    }
}
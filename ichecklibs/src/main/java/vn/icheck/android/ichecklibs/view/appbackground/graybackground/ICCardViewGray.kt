package vn.icheck.android.ichecklibs.view.appbackground.graybackground

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import vn.icheck.android.ichecklibs.Constant

class ICCardViewGray : CardView {
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
        setCardBackgroundColor(Constant.getAppBackgroundGrayColor(context))
    }
}
package vn.icheck.android.ichecklibs.view.accent.accentblue

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.SizeHelper

class ButtonAccentBlueBorder20 : androidx.appcompat.widget.AppCompatButton {

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
        val drawble = GradientDrawable().also {
            it.cornerRadius = SizeHelper.size20.toFloat()
            it.setColor(Constant.getAccentBlueColor(context))
        }
        setBackgroundDrawable(drawble)
    }
}
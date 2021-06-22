package vn.icheck.android.ichecklibs.view.line_color

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.view.disable_text.EdittextDisableHint

class ViewLineDotted : View {
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
         background = GradientDrawable().apply {
            shape = GradientDrawable.LINE
            setStroke(SizeHelper.size1, ColorManager.getLineColor(context), 10f, 10f)
        }
    }
}
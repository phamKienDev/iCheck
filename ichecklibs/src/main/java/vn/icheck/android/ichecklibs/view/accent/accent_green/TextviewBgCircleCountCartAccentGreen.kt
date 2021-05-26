package vn.icheck.android.ichecklibs.view.accent.accent_green

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ichecklibs.SizeHelper

class TextviewBgCircleCountCartAccentGreen : AppCompatTextView {
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
        val backgroundWhite=ShapeDrawable(OvalShape()).also {
            it.intrinsicWidth=SizeHelper.size22
            it.intrinsicHeight=SizeHelper.size22
        }


        includeFontPadding = false
    }
}
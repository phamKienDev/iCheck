package vn.icheck.android.ichecklibs.view.line_color

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageView
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.view.normal_text.CornerErrorEdittextNormal

class ImageViewBgTransparentRadius4 : ImageView {
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
        background = ViewHelper.bgWhiteRadius40StrokeLineColor1(context)
    }
}
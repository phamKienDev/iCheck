package vn.icheck.android.ichecklibs.view.line_color

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.view.EditTextBody1

class EdittextBody1BgTransparentStrokeLineColor1Corners4:EditTextBody1 {
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
        background=ViewHelper.bgTransparentStrokeLineColor1Corners4(context)
    }
}
package vn.icheck.android.ichecklibs.view.line_color

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.view.TextBarlowMedium
import vn.icheck.android.ichecklibs.view.disable_text.TextNormalBarlowMediumHintDisable
import vn.icheck.android.ichecklibs.view.normal_text.TextNormalBarlowMedium

class LinearLayoutBgWhiteRadius4Stroke05 : LinearLayout {
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
        background = ViewHelper.bgWhiteRadius4StrokeLineColor0_5(context)
    }
}
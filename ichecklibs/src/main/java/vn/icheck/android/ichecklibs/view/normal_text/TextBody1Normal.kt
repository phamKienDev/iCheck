package vn.icheck.android.ichecklibs.view.normal_text

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.view.TextBody1
import vn.icheck.android.ichecklibs.view.TextCaption

class TextBody1Normal : TextBody1 {

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
        setTextColor(Constant.getNormalTextColor(context))
    }
}
package vn.icheck.android.ichecklibs.view.normal_text

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.view.TextHeaderPrimary

class TextHeaderNormalTextColor : AppCompatTextView {

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
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        typeface = ResourcesCompat.getFont(context, R.font.barlow_semi_bold)
        setTextColor(if (Constant.normalTextColor.isNotEmpty()) {
            Color.parseColor(Constant.normalTextColor)
        } else {
            ContextCompat.getColor(context, R.color.colorNormalText)
        })
        includeFontPadding = false
    }
}
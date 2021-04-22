package vn.icheck.android.ichecklibs.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R

class ButtonPrimary : AppCompatTextView {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setTextColor(if (Constant.primaryColor.isNotEmpty()) {
            Color.parseColor(Constant.primaryColor)
        } else {
            ContextCompat.getColor(context, R.color.colorPrimary)
        })
    }
}
package vn.icheck.android.ichecklibs.view.primary

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import vn.icheck.android.ichecklibs.ColorManager

class EditTextPrimary : AppCompatEditText {

    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    private fun setup() {
        setTextColor(ColorManager.getPrimaryColor(context))
        includeFontPadding = false
        setHintTextColor(ColorManager.getSecondTextColor(context))
    }
}
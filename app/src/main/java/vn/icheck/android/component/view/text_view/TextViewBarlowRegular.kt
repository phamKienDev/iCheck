package vn.icheck.android.component.view.text_view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import vn.icheck.android.R

class TextViewBarlowRegular : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        typeface = ResourcesCompat.getFont(context, R.font.barlow_regular)
        includeFontPadding = false
    }
}
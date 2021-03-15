package vn.icheck.android.component.view.text_view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatTextView

class CheckedTextViewBarlowMedium : AppCompatCheckedTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        includeFontPadding = false
    }
}
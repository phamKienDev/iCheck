package vn.icheck.android.component.view.text_view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.appcompat.widget.AppCompatTextView

class CheckedTextViewBarlowMedium(context: Context, attrs: AttributeSet? = null) : AppCompatCheckedTextView(context, attrs) {
    init {
        typeface = Typeface.createFromAsset(getContext().assets, "font/barlow_medium.ttf")
        includeFontPadding = false
    }
}
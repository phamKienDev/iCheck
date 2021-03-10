package vn.icheck.android.component.view.text_view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextViewBarlowMedium(context: Context?, attrs: AttributeSet? = null) : AppCompatTextView(context!!, attrs) {
    init {
        typeface = Typeface.createFromAsset(getContext().assets, "font/barlow_medium.ttf")
    }
}
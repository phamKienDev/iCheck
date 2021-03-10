package vn.icheck.android.ui.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EditTextRoboto(context: Context?, attrs: AttributeSet?) : AppCompatEditText(context!!, attrs) {
    init {
        typeface = Typeface.createFromAsset(getContext().assets, "fonts/Roboto_Regular.ttf")
    }
}
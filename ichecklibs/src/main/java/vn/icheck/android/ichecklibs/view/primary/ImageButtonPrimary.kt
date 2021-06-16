package vn.icheck.android.ichecklibs.view.primary

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableColor

class ImageButtonPrimary : AppCompatImageButton {
    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    fun setup() {
        setBackgroundColor(Color.TRANSPARENT)
        fillDrawableColor()
    }
}
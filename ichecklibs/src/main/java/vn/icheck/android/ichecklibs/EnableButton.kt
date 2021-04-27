package vn.icheck.android.ichecklibs

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import vn.icheck.android.ichecklibs.util.toPx

class EnableButton : AppCompatButton {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    fun initView() {
        disable()
        typeface = ResourcesCompat.getFont(context, R.font.barlow_medium)
        setPadding(8.toPx(resources))
        includeFontPadding = false
        transformationMethod = null
    }

    private val enableBackground = ViewHelper.bgPrimaryCorners4(context)
    private val disableBackground = ViewHelper.bgDisableTextCorners4(context)

    fun enable() {
        isEnabled = true
        changeBackground()
    }

    fun disable() {
        isEnabled = false
        changeBackground()
    }

    private fun changeBackground() {
        background = if (isEnabled) {
            enableBackground
        } else {
            disableBackground
        }
    }
}
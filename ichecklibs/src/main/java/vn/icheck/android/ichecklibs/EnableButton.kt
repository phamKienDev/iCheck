package vn.icheck.android.ichecklibs

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import vn.icheck.android.ichecklibs.util.dpToPx

class EnableButton: AppCompatButton {
    constructor(context: Context):super(context){
        initView()
    }

    constructor(context: Context, attrs: AttributeSet):super(context, attrs){
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):super(context, attrs,R.style.EnableButtonTheme){
        initView()
    }

    fun initView() {
        isEnabled = false
        typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        setPadding(8.dpToPx())
        includeFontPadding = false
    }

    private val enableBackground = ViewHelper.bgPrimaryCorners4(context)
    private val disableBackground =  ViewHelper.bgDisableTextCorners4(context)

    private var isButtonEnable = false
        set(value) {
            isEnabled = value
            field = value
        }

    fun enable() {
        isButtonEnable = true
        changeBackground()
    }

    fun disable() {
        isButtonEnable = false
        changeBackground()
    }

    private fun changeBackground() {
        background = if (isButtonEnable) {
            enableBackground
        } else {
            disableBackground
        }
    }
}
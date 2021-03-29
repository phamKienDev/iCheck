package vn.icheck.android.ichecklibs

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding

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
        setPadding(8.toPx(resources))
        includeFontPadding = false
    }

    private val enableBackground =     ResourcesCompat.getDrawable(resources, R.drawable.background_button_enable, null)

    private val disableBackground =   ResourcesCompat.getDrawable(resources, R.drawable.background_button_disable, null)

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
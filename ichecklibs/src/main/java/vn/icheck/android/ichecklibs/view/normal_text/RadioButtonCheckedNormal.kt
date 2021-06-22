package vn.icheck.android.ichecklibs.view.normal_text

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioButton
import vn.icheck.android.ichecklibs.ColorManager

class RadioButtonCheckedNormal: RadioButton {
    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    private fun setup() {
        setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setTextColor(ColorManager.getNormalTextColor(context))
            }else{
                setTextColor(ColorManager.getSecondTextColor(context))
            }
        }
        setTextColor(ColorManager.getSecondTextColor(context))

        includeFontPadding = false
    }
}
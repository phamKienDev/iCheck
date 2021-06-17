package vn.icheck.android.ichecklibs.view.normal_text

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R

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
                setTextColor(if (Constant.normalTextColor.isNotEmpty()) {
                    Color.parseColor(Constant.normalTextColor)
                } else {
                    ContextCompat.getColor(context, R.color.colorNormalText)
                })
            }else{
                setTextColor(if (Constant.secondTextColor.isNotEmpty()) {
                    Color.parseColor(Constant.secondTextColor)
                } else {
                    ContextCompat.getColor(context, R.color.colorSecondText)
                })
            }
        }
        setTextColor(if (Constant.secondTextColor.isNotEmpty()) {
            Color.parseColor(Constant.secondTextColor)
        } else {
            ContextCompat.getColor(context, R.color.colorSecondText)
        })

        includeFontPadding = false
    }
}
package vn.icheck.android.ichecklibs.view.disable_text

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.ViewHelper

class RadiobuttonDisableUncheckYellowChecked : RadioButton {

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setup()
    }

    private fun setup() {
        setTextColor(
            ViewHelper.createColorStateList(
                ColorManager.getDisableTextColor(context),
                ContextCompat.getColor(context, R.color.warning_scan_buy)
            )
        )
    }
}
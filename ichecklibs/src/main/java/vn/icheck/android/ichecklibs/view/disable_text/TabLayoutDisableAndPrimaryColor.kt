package vn.icheck.android.ichecklibs.view.disable_text

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R

class TabLayoutDisableAndPrimaryColor  : TabLayout {

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
        setTabTextColors(Constant.getDisableTextColor(context),Constant.getPrimaryColor(context))
    }
}
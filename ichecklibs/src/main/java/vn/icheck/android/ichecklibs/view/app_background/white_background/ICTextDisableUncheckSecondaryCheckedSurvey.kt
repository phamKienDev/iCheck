package vn.icheck.android.ichecklibs.view.app_background.white_background

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.view.disable_text.TextDisableUncheckSecondaryChecked

//view dùng cho khảo sát
class ICTextDisableUncheckSecondaryCheckedSurvey : TextDisableUncheckSecondaryChecked {

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
        background = ViewHelper.bgWhiteDetailSurveyOption(context)
    }
}
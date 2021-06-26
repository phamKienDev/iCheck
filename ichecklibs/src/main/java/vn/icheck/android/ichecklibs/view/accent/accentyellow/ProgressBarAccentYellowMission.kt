package vn.icheck.android.ichecklibs.view.accent.accentyellow

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import vn.icheck.android.ichecklibs.ViewHelper

class ProgressBarAccentYellowMission : ProgressBar {
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
        progressDrawable = ViewHelper.progressbarAccentYellowMission(context)
    }
}
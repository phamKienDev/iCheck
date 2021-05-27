package vn.icheck.android.ichecklibs.view.accent.accent_yellow

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ProgressBar
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.SizeHelper
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
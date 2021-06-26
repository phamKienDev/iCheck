package vn.icheck.android.ichecklibs.view.accent.accentblue

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ProgressBar
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.SizeHelper

class ProgressBarAccentBlue : ProgressBar {
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
        val background = GradientDrawable().also {
            it.setColor(Color.parseColor("#D8D8D8"))
            it.cornerRadius = SizeHelper.size8.toFloat()
        }


        val progressDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    ColorManager.getPrimaryColor(context),
                    ColorManager.getAccentBlueColor(context)
                )).also { it.cornerRadius = SizeHelper.size8.toFloat() }
        val progress = ScaleDrawable(progressDrawable, Gravity.START, 1f, 0.1f)

        val layers = arrayOf(background, progress)
        val layerDrawble = LayerDrawable(layers)
        layerDrawble.setId(0, android.R.id.background)
        layerDrawble.setId(1, android.R.id.progress)

        setProgressDrawable(layerDrawble)
    }
}
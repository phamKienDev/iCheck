package vn.icheck.android.ichecklibs.view.accent.accent_blue

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.widget.ProgressBar
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
                        Constant.getPrimaryColor(context),
                        Constant.getAccentBlueColor(context)
                )).also { it.cornerRadius = SizeHelper.size8.toFloat() }

        val progress = ScaleDrawable(progressDrawable,0,100f,0f)

        val layers = arrayOf(background, progress)

        val layerDrawble = LayerDrawable(layers)
        layerDrawble.setDrawableByLayerId(android.R.id.background, background)
        layerDrawble.setDrawableByLayerId(android.R.id.progress, progress)
    }
}
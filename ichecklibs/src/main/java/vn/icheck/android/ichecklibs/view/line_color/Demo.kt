package vn.icheck.android.ichecklibs.view.line_color

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.SizeHelper

class Demo : LinearLayout {
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

        val layers = arrayOf(background)
        val layerDrawble = LayerDrawable(layers)
        layerDrawble.setId(0, android.R.id.background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawble.setLayerInsetStart(0,50)
        }
        layerDrawble.setPadding(0,0,0,0)
        layerDrawble.setLayerInsetBottom(0,50)

        setBackground(layerDrawble)
    }
}
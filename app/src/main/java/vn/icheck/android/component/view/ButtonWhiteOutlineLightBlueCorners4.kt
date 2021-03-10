package vn.icheck.android.component.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper

/**
 * Created by VuLCL on 04/27/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 *
 * Button có giao diện mặc định:
 * Text color: Light blue
 * Text size: 16sp
 * Background: White, outline 1dp - light blue, corners 4dp
 *
 * Thuộc tính có thể thay đổi
 * app:buttonTextColor - Thay đổi màu của text
 * app:buttonTextSize - Thay đổi size của text
 */
class ButtonWhiteOutlineLightBlueCorners4 : BaseButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSetupDefault() {
        defaultTextColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(ContextCompat.getColor(context, R.color.lightBlue)))

        defaultDisableBackground = ContextCompat.getColor(context, R.color.gray)
        defaultEnableBackground = Color.WHITE
        defaultPressedBackground = ContextCompat.getColor(context, R.color.black_10)

        defaultStrokeWidth = SizeHelper.size1
        defaultStrokeColor = ContextCompat.getColor(context, R.color.lightBlue)
        defaultRadius = SizeHelper.size4.toFloat()
    }
}
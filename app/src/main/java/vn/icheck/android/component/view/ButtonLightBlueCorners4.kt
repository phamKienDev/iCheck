package vn.icheck.android.component.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper

/**
 * Created by VuLCL on 04/27/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 *
 * Button có giao diện mặc định:
 * Text color: White
 * Text size: 16sp
 * Background: Light blue, corners 4dp
 *
 * Thuộc tính có thể thay đổi
 * app:buttonTextColor - Thay đổi màu của text
 * app:buttonTextSize - Thay đổi size của text
 */
class ButtonLightBlueCorners4 : BaseButton {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSetupDefault() {
        defaultTextColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.WHITE))

        defaultDisableBackground = ContextCompat.getColor(context, R.color.grayD8)
        defaultEnableBackground = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(context)
        defaultPressedBackground = vn.icheck.android.ichecklibs.Constant.getSecondaryColor(context)

        defaultRadius = SizeHelper.size4.toFloat()
    }
}
package vn.icheck.android.ichecklibs

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import vn.icheck.android.ichecklibs.util.dpToPx
import kotlin.math.roundToInt


object ViewHelper {
    fun createLayoutParams(width: Int, height: Int, weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height, weight)
    }

    fun createLayoutParams(width: Int, height: Int = LinearLayout.LayoutParams.WRAP_CONTENT): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height)
    }

    fun createColorStateList(unCheckColor: Int, checkedColor: Int): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)), intArrayOf(unCheckColor, checkedColor))
    }

    fun createColorStateList(disableColor: Int, enableColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled), intArrayOf(android.R.attr.state_pressed)),
                intArrayOf(disableColor, enableColor, pressedColor))
    }

    @ColorInt
    fun alphaColor(@ColorInt color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    private fun createBackground(color: Int, corners: Float): GradientDrawable {
        return GradientDrawable().also {
            it.setColor(color)
            it.cornerRadius = corners.dpToPx()
        }
    }

    private fun createBackground(color: Int, corners: Float, strokeWidth: Int, strokeColor: Int): GradientDrawable {
        return GradientDrawable().also {
            it.setColor(color)
            it.cornerRadius = corners.dpToPx()
            it.setStroke(strokeWidth, strokeColor)
        }
    }

    /*
    * Primary Color
    * */
    fun textColorDisableTextUncheckPrimaryChecked(context: Context): ColorStateList {
        return createColorStateList(
                ContextCompat.getColor(context, R.color.colorDisableText),
                Constant.getPrimaryColor(context)
        )
    }

    fun bgPrimaryCorners4(context: Context) = createBackground(Constant.getPrimaryColor(context), 4f)

    fun bgPrimaryCorners16(context: Context) = createBackground(Constant.getPrimaryColor(context), 16f)

    fun bgPrimaryCorners18(context: Context) = createBackground(Constant.getPrimaryColor(context), 18f)

    fun bgPrimaryCorners47(context: Context) = createBackground(Constant.getPrimaryColor(context), 47f)

    fun bgPrimaryOutline3Corners20(context: Context): Drawable {
        val primaryColor = Constant.getPrimaryColor(context)
        return createBackground(primaryColor, 20f, SizeHelper.size3, alphaColor(primaryColor, 0.4f))
    }

    /*
    * White
    * */
    fun bgWhiteOutlinePrimary1Corners4(context: Context) = createBackground(Color.WHITE, 4f, SizeHelper.size1, Constant.getPrimaryColor(context))

    fun bgWhiteOutlinePrimary2Corners16(context: Context) = createBackground(Color.WHITE, 16f, SizeHelper.size2, Constant.getPrimaryColor(context))

    /*
    * Transparent
    * */
    fun bgOutlinePrimary1Corners4(context: Context) = createBackground(Color.TRANSPARENT, 4f, SizeHelper.size1, Constant.getPrimaryColor(context))

    fun bgOutlinePrimary1Corners6(context: Context) = createBackground(Color.TRANSPARENT, 6f, SizeHelper.size1, Constant.getPrimaryColor(context))

    fun bgOutlinePrimary05Corners14(context: Context) = createBackground(Color.TRANSPARENT, 14f, SizeHelper.size0_5, Constant.getPrimaryColor(context))

    /*
    * Disable Text Color
    * */
    fun bgDisableTextCorners4(context: Context) = createBackground(ContextCompat.getColor(context, R.color.colorDisableText), 4f)
}
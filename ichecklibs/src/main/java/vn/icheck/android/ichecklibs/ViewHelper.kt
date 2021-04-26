package vn.icheck.android.ichecklibs

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.LinearLayout
import vn.icheck.android.ichecklibs.util.dpToPx

object ViewHelper {
    fun createLayoutParams(width: Int, height: Int, weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height, weight)
    }

    fun createLayoutParams(width: Int, height: Int = LinearLayout.LayoutParams.WRAP_CONTENT): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height)
    }

    fun textColorHomeTab(context: Context): ColorStateList {
        return createColorStateList(
                Constant.getDisableTextColor(context),
                Constant.getPrimaryColor(context)
        )
    }

    fun createColorStateList(unCheckColor: Int, checkedColor: Int): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)), intArrayOf(unCheckColor, checkedColor))
    }

    fun createColorStateList(disableColor: Int, enableColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled), intArrayOf(android.R.attr.state_pressed)),
                intArrayOf(disableColor, enableColor, pressedColor))
    }

    fun backgroundPrimaryCorners4(context: Context): Drawable {
        return GradientDrawable().also {
            it.setColor(Constant.getPrimaryColor(context))
            it.cornerRadius = 4f.dpToPx()
        }
    }

    fun backgroundDisableTextCorners4(context: Context): Drawable {
        return GradientDrawable().also {
            it.setColor(Constant.getDisableTextColor(context))
            it.cornerRadius = 4f.dpToPx()
        }
    }
}
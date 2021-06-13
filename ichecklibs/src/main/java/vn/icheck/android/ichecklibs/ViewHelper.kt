package vn.icheck.android.ichecklibs

import android.R
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.widget.LinearLayout

object ViewHelper {
    fun createLayoutParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    fun createLayoutParams(width: Int, height: Int = LinearLayout.LayoutParams.WRAP_CONTENT): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height)
    }

    fun createLayoutParams(width: Int, height: Int, weight: Float): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height, weight)
    }

    fun createColorStateList(unCheckColor: Int, checkedColor: Int): ColorStateList {
        return ColorStateList(
                arrayOf(
                        intArrayOf(-R.attr.state_checked),
                        intArrayOf(R.attr.state_checked),
                        intArrayOf(-R.attr.state_selected),
                        intArrayOf(R.attr.state_selected)
                ), intArrayOf(unCheckColor, checkedColor, unCheckColor, checkedColor)
        )
    }

    fun createCheckedDrawable(uncheckedResource: Drawable?, checkedResource: Drawable?): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        statesListDrawable.addState(
                intArrayOf(-android.R.attr.state_checked),
                uncheckedResource
        )
        statesListDrawable.addState(
                intArrayOf(android.R.attr.state_checked),
                checkedResource
        )

        return statesListDrawable
    }
}
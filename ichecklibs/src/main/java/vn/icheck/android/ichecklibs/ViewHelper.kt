package vn.icheck.android.ichecklibs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.view.Gravity
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

//    private fun createBackground(color: Int, corners: Float): GradientDrawable {
//        return GradientDrawable().also {
//            it.setColor(color)
//            it.cornerRadius = corners.dpToPx()
//        }
//    }
//
//    private fun createBackground(color: Int, corners: Float, strokeWidth: Int, strokeColor: Int): GradientDrawable {
//        return GradientDrawable().also {
//            it.setColor(color)
//            it.cornerRadius = corners.dpToPx()
//            it.setStroke(strokeWidth, strokeColor)
//        }
//    }

    /*
    * RippleDrawable
    * */
    @SuppressLint("NewApi")
    fun createRippleDrawable(context: Context, drawable: Drawable): RippleDrawable {
        return RippleDrawable(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_10)), drawable, null)
    }

    @SuppressLint("NewApi")
    fun createRippleDrawable(context: Context, color: Int, strokeWidth: Int, strokeColor: Int, radius: Float): RippleDrawable {
        return RippleDrawable(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_10)),
                createShapeDrawable(color, strokeWidth, strokeColor, radius), null)
    }

    /*
    * GradientDrawable
    * */
    fun createShapeDrawable(color: Int, radius: Float): GradientDrawable {
        return GradientDrawable().also {
            it.setColor(color)
            it.cornerRadius = radius
        }
    }

    fun createShapeDrawable(color: Int, strokeWidth: Int, strokeColor: Int, radius: Float): GradientDrawable {
        return GradientDrawable().also {
            it.setColor(color)
            it.setStroke(strokeWidth, strokeColor)
            it.cornerRadius = radius
        }
    }

    fun createShapeDrawable(color: Int, strokeWidth: Int, strokeColor: Int, radiusTopLeft: Float, radiusTopLRight: Float, radiusBottomRight: Float, radiusBottomLeft: Float): GradientDrawable {
        return GradientDrawable().also { gradientDrawable ->
            gradientDrawable.setColor(color)
            gradientDrawable.setStroke(strokeWidth, strokeColor)
            gradientDrawable.cornerRadii = floatArrayOf(radiusTopLeft, radiusTopLeft, radiusTopLRight, radiusTopLRight, radiusBottomRight, radiusBottomRight, radiusBottomLeft, radiusBottomLeft)
        }
    }

    fun createScaleDrawable(drawable: Drawable, gravity: Int = Gravity.START): ScaleDrawable {
        return ScaleDrawable(drawable, gravity, 1f, 0f)
    }

    /*
    * StateListDrawable
    * */
    fun createButtonStateListDrawable(context: Context,
                                      enableColor: Int, pressedColor: Int, disableColor: Int,
                                      enableStrokeColor: Int, pressedStrokeColor: Int, disableStrokeColor: Int,
                                      strokeWidth: Int, radius: Float): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled), createRippleDrawable(context, enableColor, strokeWidth, enableStrokeColor, radius))
            statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), createShapeDrawable(disableColor, strokeWidth, disableStrokeColor, radius))
        } else {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled), createShapeDrawable(enableColor, strokeWidth, enableStrokeColor, radius))
            statesListDrawable.addState(intArrayOf(android.R.attr.state_pressed), createShapeDrawable(pressedColor, strokeWidth, pressedStrokeColor, radius))
            statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), createShapeDrawable(disableColor, strokeWidth, disableStrokeColor, radius))
        }

        return statesListDrawable
    }

    fun createProgressStateListDrawable(backgroundDrawable: Drawable, secondaryDrawable: Drawable, progressDrawable: Drawable): LayerDrawable {
        val layerDrawable = LayerDrawable(arrayOf(backgroundDrawable, secondaryDrawable, progressDrawable))

        layerDrawable.setId(0,android.R.id.background)
        layerDrawable.setId(1,android.R.id.secondaryProgress)
        layerDrawable.setId(2,android.R.id.progress)

        return layerDrawable
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

    fun bgPrimaryCorners4(context: Context) = createShapeDrawable(Constant.getPrimaryColor(context), 4f)

    fun bgPrimaryCorners16(context: Context) = createShapeDrawable(Constant.getPrimaryColor(context), 16f)

    fun bgPrimaryCorners18(context: Context) = createShapeDrawable(Constant.getPrimaryColor(context), 18f)

    fun bgPrimaryCorners47(context: Context) = createShapeDrawable(Constant.getPrimaryColor(context), 47f)

    fun bgPrimaryOutline3Corners20(context: Context): Drawable {
        val primaryColor = Constant.getPrimaryColor(context)
        return createShapeDrawable(primaryColor, SizeHelper.size3, alphaColor(primaryColor, 0.4f), 20f)
    }

    fun btnPrimaryCorners4(context: Context): Drawable {
        return createButtonStateListDrawable(context = context,
                enableColor = Constant.getPrimaryColor(context),
                pressedColor = Constant.getSecondaryColor(context),
                disableColor = ContextCompat.getColor(context, R.color.gray),
                enableStrokeColor = Color.TRANSPARENT,
                pressedStrokeColor = Color.TRANSPARENT,
                disableStrokeColor = Color.TRANSPARENT,
                strokeWidth = Color.TRANSPARENT,
                4f.dpToPx()
        )
    }

    fun progressPrimaryBackgroundTransparentCorners8(context: Context): LayerDrawable {
        val radius = 8f.dpToPx()
        val primaryColor = Constant.getPrimaryColor(context)
        return createProgressStateListDrawable(
                createShapeDrawable(Color.TRANSPARENT, radius),
                createScaleDrawable(createShapeDrawable(primaryColor, radius)),
                createScaleDrawable(createShapeDrawable(primaryColor, radius))
        )
    }

    /*
    * White
    * */
    fun bgWhiteOutlinePrimary1Corners4(context: Context) = createShapeDrawable(Color.WHITE, SizeHelper.size1, Constant.getPrimaryColor(context), 4f)

    fun bgWhiteOutlinePrimary2Corners16(context: Context) = createShapeDrawable(Color.WHITE, SizeHelper.size2, Constant.getPrimaryColor(context), 16f)

    /*
    * Transparent
    * */
    fun bgOutlinePrimary1Corners4(context: Context) = createShapeDrawable(Color.TRANSPARENT, SizeHelper.size1, Constant.getPrimaryColor(context), 4f)

    fun bgOutlinePrimary1Corners6(context: Context) = createShapeDrawable(Color.TRANSPARENT, SizeHelper.size1, Constant.getPrimaryColor(context), 6f)

    fun bgOutlinePrimary05Corners14(context: Context) = createShapeDrawable(Color.TRANSPARENT, SizeHelper.size0_5, Constant.getPrimaryColor(context), 14f)

    /*
    * Disable Text Color
    * */
    fun bgDisableTextCorners4(context: Context) = createShapeDrawable(ContextCompat.getColor(context, R.color.colorDisableText), 4f)
}
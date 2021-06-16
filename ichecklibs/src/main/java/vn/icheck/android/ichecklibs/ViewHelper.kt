package vn.icheck.android.ichecklibs

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.view.Gravity
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.OvalShape
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import vn.icheck.android.ichecklibs.util.dpToPx
import kotlin.math.roundToInt


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
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(unCheckColor, checkedColor)
        )
    }

    fun createColorStateFocusedList(unFocusColor: Int, focusedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_focused)
            ), intArrayOf(unFocusColor, focusedColor)
        )
    }

    fun createColorStateList(disableColor: Int, enableColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled), intArrayOf(android.R.attr.state_pressed)
            ),
            intArrayOf(disableColor, enableColor, pressedColor)
        )
    }

    fun createColorPressStateList(disableColor: Int, pressedColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed)),
            intArrayOf(disableColor, pressedColor)
        )
    }


    fun createCheckedDrawable(uncheckedResource: Drawable?, checkedResource: Drawable?): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        statesListDrawable.addState(intArrayOf(-android.R.attr.state_checked), uncheckedResource)
        statesListDrawable.addState(intArrayOf(android.R.attr.state_checked), checkedResource)

        return statesListDrawable
    }

    fun createPressDrawable(unPressResource: Drawable?, pressResource: Drawable?): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        statesListDrawable.addState(intArrayOf(-android.R.attr.state_pressed), unPressResource)
        statesListDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressResource)

        return statesListDrawable
    }

    fun createPressDrawable(context: Context, pressResource: Drawable): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled), createRippleDrawable(context, pressResource))
        } else {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressResource)
        }

        return statesListDrawable
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
        return RippleDrawable(
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_10)), drawable, null
        )
    }

    @SuppressLint("NewApi")
    fun createRippleDrawable(context: Context, color: Int, radius: Float): RippleDrawable {
        return RippleDrawable(
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_10)), createShapeDrawable(color, radius), null
        )
    }

    @SuppressLint("NewApi")
    fun createRippleDrawable(context: Context, color: Int, strokeWidth: Int, strokeColor: Int, radius: Float): RippleDrawable {
        return RippleDrawable(
            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black_10)),
            createShapeDrawable(color, strokeWidth, strokeColor, radius), null
        )
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

    fun createShapeDrawableRadius(floatArray: FloatArray, color: Int, strokeWidth: Int, strokeColor: Int): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadii = floatArray
            setColor(color)
            setStroke(strokeWidth, strokeColor)
        }
    }

    fun createShapeDrawableRadius(floatArray: FloatArray, color: Int): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadii = floatArray
            setColor(color)
        }
    }


    fun createShapeDrawableRadiusTop(color: Int, strokeWidth: Int, strokeColor: Int, radiusTop: Float): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadii = floatArrayOf(
                // top left
                radiusTop,
                radiusTop,
                // top right
                radiusTop,
                radiusTop,
                // bottom right
                0f,
                0f,
                // bottom left
                0f,
                0f
            )
            setColor(color)
            setStroke(strokeWidth, strokeColor)
        }
    }

    fun createShapeDrawableRadiusTop(color: Int, radiusTop: Float): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadii = floatArrayOf(
                // top left
                radiusTop,
                radiusTop,
                // top right
                radiusTop,
                radiusTop,
                // bottom right
                0f,
                0f,
                // bottom left
                0f,
                0f
            )
            setColor(color)
        }
    }


    fun createShapeDrawableRadiusBottom(color: Int, strokeWidth: Int, strokeColor: Int, radiusBottom: Float): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadii = floatArrayOf(
                // top left
                0f,
                0f,
                // top right
                0f,
                0f,
                // bottom right
                radiusBottom,
                radiusBottom,
                // bottom left
                radiusBottom,
                radiusBottom
            )
            setColor(color)
            setStroke(strokeWidth, strokeColor)
        }
    }

    fun createShapeDrawableRadiusBottom(color: Int, radiusBottom: Float): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadii = floatArrayOf(
                // top left
                0f,
                0f,
                // top right
                0f,
                0f,
                // bottom right
                radiusBottom,
                radiusBottom,
                // bottom left
                radiusBottom,
                radiusBottom
            )
            setColor(color)
        }
    }

    fun createShapeDrawableRadiusLeft(color: Int, strokeWidth: Int, strokeColor: Int, radiusLeft: Float): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadii = floatArrayOf(
                // top left
                radiusLeft,
                radiusLeft,
                // top right
                0f,
                0f,
                // bottom right
                0f,
                0f,
                // bottom left
                radiusLeft,
                radiusLeft
            )
            setColor(color)
            setStroke(strokeWidth, strokeColor)
        }
    }

    fun createShapeDrawableRadiusLeft(color: Int, radiusLeft: Float): GradientDrawable {
        return GradientDrawable().apply {
            cornerRadii = floatArrayOf(
                // top left
                radiusLeft,
                radiusLeft,
                // top right
                0f,
                0f,
                // bottom right
                0f,
                0f,
                // bottom left
                radiusLeft,
                radiusLeft
            )
            setColor(color)
        }
    }

    fun createShapeDrawable(
        color: Int,
        strokeWidth: Int,
        strokeColor: Int,
        radiusTopLeft: Float,
        radiusTopLRight: Float,
        radiusBottomRight: Float,
        radiusBottomLeft: Float
    ): GradientDrawable {
        return GradientDrawable().also { gradientDrawable ->
            gradientDrawable.setColor(color)
            gradientDrawable.setStroke(strokeWidth, strokeColor)
            gradientDrawable.cornerRadii = floatArrayOf(
                radiusTopLeft,
                radiusTopLeft,
                radiusTopLRight,
                radiusTopLRight,
                radiusBottomRight,
                radiusBottomRight,
                radiusBottomLeft,
                radiusBottomLeft
            )
        }
    }

    fun createScaleDrawable(drawable: Drawable, gravity: Int = Gravity.START): ScaleDrawable {
        return ScaleDrawable(drawable, gravity, 1f, 0f)
    }


    /*
    * SET màu drawable
    * */
    fun setImageColor(drawable: Drawable,color: Int): Drawable {
        DrawableCompat.setTint(drawable, color)
        return drawable
    }

    fun setImageColor(icon: Int, context: Context,color: Int): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, color)
        }
        return icon
    }

    /*
    * StateListDrawable
    * */
    fun createButtonStateListDrawable(context: Context, enableColor: Int, pressedColor: Int, disableColor: Int, radius: Float): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled),
                createRippleDrawable(context, enableColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(-android.R.attr.state_enabled),
                createShapeDrawable(disableColor, radius)
            )
        } else {
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_pressed),
                createShapeDrawable(enableColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
                createShapeDrawable(pressedColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(-android.R.attr.state_enabled),
                createShapeDrawable(disableColor, radius)
            )
        }

        return statesListDrawable
    }

    fun createButtonStateListDrawable(
        context: Context,
        enableColor: Int,
        pressedColor: Int,
        disableColor: Int,
        enableStrokeColor: Int,
        pressedStrokeColor: Int,
        disableStrokeColor: Int,
        strokeWidth: Int,
        radius: Float
    ): StateListDrawable {
        val statesListDrawable = StateListDrawable()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled),
                createRippleDrawable(context, enableColor, strokeWidth, enableStrokeColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(-android.R.attr.state_enabled),
                createShapeDrawable(disableColor, strokeWidth, disableStrokeColor, radius)
            )
        } else {
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_enabled),
                createShapeDrawable(enableColor, strokeWidth, enableStrokeColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(android.R.attr.state_pressed),
                createShapeDrawable(pressedColor, strokeWidth, pressedStrokeColor, radius)
            )
            statesListDrawable.addState(
                intArrayOf(-android.R.attr.state_enabled),
                createShapeDrawable(disableColor, strokeWidth, disableStrokeColor, radius)
            )
        }

        return statesListDrawable
    }

    fun createProgressStateListDrawable(backgroundDrawable: Drawable, secondaryDrawable: Drawable, progressDrawable: Drawable): LayerDrawable {
        val layerDrawable =
            LayerDrawable(arrayOf(backgroundDrawable, secondaryDrawable, progressDrawable))

        layerDrawable.setId(0, android.R.id.background)
        layerDrawable.setId(1, android.R.id.secondaryProgress)
        layerDrawable.setId(2, android.R.id.progress)

        return layerDrawable
    }


    fun getDrawableFillColor(context: Context, resource: Int, color: String): Drawable? {
        return ContextCompat.getDrawable(context, resource)?.apply {
            DrawableCompat.setTint(this, Color.parseColor(color))
        }
    }

    /*
    * Primary Color
    * */
    fun textColorDisableTextUncheckPrimaryChecked(context: Context): ColorStateList {
        return createColorStateList(
            Constant.getDisableTextColor(context),
            Constant.getPrimaryColor(context)
        )
    }

    fun bgPrimaryCorners4(context: Context) = createShapeDrawable(Constant.getPrimaryColor(context), SizeHelper.size4.toFloat())

    fun bgPrimaryCorners16(context: Context) = createShapeDrawable(Constant.getPrimaryColor(context), SizeHelper.size16.toFloat())

    fun bgPrimaryCorners18(context: Context) = createShapeDrawable(Constant.getPrimaryColor(context), SizeHelper.size18.toFloat())

    fun bgPrimaryCornersTop4(context: Context) = createShapeDrawableRadiusTop(Constant.getPrimaryColor(context), SizeHelper.size4.toFloat())

    fun bgPrimaryOutline3Corners20(context: Context): Drawable {
        val primaryColor = Constant.getPrimaryColor(context)
        return createShapeDrawable(
            color = primaryColor,
            strokeWidth = SizeHelper.size3,
            strokeColor = alphaColor(primaryColor, 0.4f),
            radius = 20f
        )
    }

    fun btnPrimaryCorners4(context: Context) = createButtonStateListDrawable(
        context = context,
        enableColor = Constant.getPrimaryColor(context),
        pressedColor = Constant.getSecondaryColor(context),
        disableColor = ContextCompat.getColor(context, R.color.grayD8),
        radius = 4f.dpToPx()
    )


    fun progressPrimaryBackgroundTransparentCorners8(context: Context): LayerDrawable {
        val radius = 8f.dpToPx()
        val primaryColor = Constant.getPrimaryColor(context)
        return createProgressStateListDrawable(
            backgroundDrawable = createShapeDrawable(Color.TRANSPARENT, radius),
            secondaryDrawable = createScaleDrawable(createShapeDrawable(primaryColor, radius)),
            progressDrawable = createScaleDrawable(createShapeDrawable(primaryColor, radius))
        )
    }

    fun setPrimaryHtmlString(string: String): String {
        return string.replace("#057DDA", Constant.getPrimaryColorCode)
    }

    fun textColorPrimaryUnpressedSecondaryPressed(context: Context): ColorStateList {
        return createColorPressStateList(Constant.getPrimaryColor(context), Constant.getSecondaryColor(context))
    }

    fun setImageColorPrimary(drawable: Drawable, context: Context): Drawable {
        DrawableCompat.setTint(drawable, Constant.getPrimaryColor(context))
        return drawable
    }

    fun setImageColorPrimary(icon: Int, context: Context): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, Constant.getPrimaryColor(context))
        }
        return icon
    }

    fun ImageView.setImageColorPrimary(icon: Int, context: Context): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, Constant.getPrimaryColor(context))
        }
        this.setImageResource(icon)
        return icon
    }

    fun AppCompatImageButton.setImageColorPrimary(icon: Int, context: Context): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, Constant.getPrimaryColor(context))
        }
        this.setImageResource(icon)
        return icon
    }

    fun setCheckedPrimary(uncheck: Drawable,checked:Drawable, context: Context): Drawable {
        DrawableCompat.setTint(uncheck, ContextCompat.getColor(context,R.color.grayB4))
        DrawableCompat.setTint(checked, Constant.getPrimaryColor(context))
        return createCheckedDrawable(uncheck, checked)
    }

    fun setCheckedPrimary(uncheck: Int,checked:Int, context: Context): Drawable {
        val drawableUnCheck=ContextCompat.getDrawable(context,uncheck)
        val drawableChecked=ContextCompat.getDrawable(context,checked)

        drawableUnCheck?.let { DrawableCompat.setTint(it, ContextCompat.getColor(context,R.color.grayB4)) }
        drawableChecked?.let { DrawableCompat.setTint(it, Constant.getPrimaryColor(context)) }

        return createCheckedDrawable(drawableUnCheck, drawableChecked)
    }

    /*
    * White
    * */
    fun bgWhiteStrokePrimary1Corners4(context: Context) =
        createShapeDrawable(Color.WHITE, SizeHelper.size1, Constant.getPrimaryColor(context), SizeHelper.size4.toFloat())

    fun bgWhiteStrokePrimary2Corners16(context: Context) =
        createShapeDrawable(Color.WHITE, SizeHelper.size2, Constant.getPrimaryColor(context), SizeHelper.size16.toFloat())

    /*
    * Transparent
    * */
    fun bgOutlinePrimary1Corners4(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getPrimaryColor(context),
        radius = SizeHelper.size4.toFloat()
    )

    fun bgOutlinePrimary1Corners6(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getPrimaryColor(context),
        radius = SizeHelper.size6.toFloat()
    )

    fun bgOutlinePrimary05Corners14(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size0_5,
        strokeColor = Constant.getPrimaryColor(context),
        radius = SizeHelper.size14.toFloat()
    )

    fun bgOutlineSecondary1Corners6(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getSecondaryColor(context),
        radius = SizeHelper.size6.toFloat()
    )

    fun bgOutlineSecondary1Corners8(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getSecondaryColor(context),
        radius = SizeHelper.size8.toFloat()
    )

    fun bgOutlineSecondary1Corners13(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getSecondaryColor(context),
        radius = SizeHelper.size13.toFloat()
    )

    fun bgOutlineSecondary1Corners45(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getSecondaryColor(context),
        radius = SizeHelper.size45.toFloat()
    )


    /*
    * Secondary Color
    * */
    fun bgSecondaryCorners6(context: Context) = createShapeDrawable(Constant.getSecondaryColor(context), SizeHelper.size6.toFloat())
    fun bgSecondaryCorners10(context: Context) = createShapeDrawable(Constant.getSecondaryColor(context), SizeHelper.size10.toFloat())
    fun bgSecondaryCorners20(context: Context) = createShapeDrawable(Constant.getSecondaryColor(context), SizeHelper.size20.toFloat())
    fun bgSecondaryCorners35(context: Context) = createShapeDrawable(Constant.getSecondaryColor(context), SizeHelper.size35.toFloat())
    fun bgSecondaryCorners40(context: Context) = createShapeDrawable(Constant.getSecondaryColor(context), SizeHelper.size40.toFloat())
    fun bgSecondaryCorners45(context: Context) = createShapeDrawable(Constant.getSecondaryColor(context), SizeHelper.size45.toFloat())

    fun bgSecondaryCornersTop10(context: Context) = createShapeDrawableRadiusTop(Constant.getSecondaryColor(context), SizeHelper.size10.toFloat())

    fun btnSecondaryCorners6(context: Context) = createButtonStateListDrawable(
        context = context,
        enableColor = Constant.getSecondaryColor(context),
        pressedColor = ContextCompat.getColor(context, R.color.darkBlue),
        disableColor = ContextCompat.getColor(context, R.color.grayD8),
        radius = 6f.dpToPx()
    )

    fun btnSecondaryCorners26(context: Context) = createButtonStateListDrawable(
        context = context,
        enableColor = Constant.getSecondaryColor(context),
        pressedColor = ContextCompat.getColor(context, R.color.darkBlue),
        disableColor = ContextCompat.getColor(context, R.color.grayD8),
        radius = 26f.dpToPx()
    )

    fun lineDottedHorizontalSecondary() = GradientDrawable().apply {
        shape = GradientDrawable.LINE
        setStroke(SizeHelper.size0_5, Color.parseColor("#D8D8D8"), SizeHelper.size4.toFloat(), SizeHelper.size6.toFloat())
    }

    fun setSecondaryHtmlString(string: String): String {
        return string.replace("#3c5a99", Constant.getSecondaryColorCode)
    }

    fun setImageColorSecondary(icon: Int, context: Context): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, Constant.getSecondaryColor(context))
        }
        return icon
    }


    fun bgGrayB4Corners4(context: Context) = createShapeDrawable(ContextCompat.getColor(context, R.color.grayB4), SizeHelper.size4.toFloat())

    /*
    * View
    * */
    fun bgPaymentState(context: Context) = createButtonStateListDrawable(
        context = context,
        enableColor = Constant.getPrimaryColor(context),
        pressedColor = Constant.getSecondaryColor(context),
        disableColor = ContextCompat.getColor(context, R.color.grayB4),
        radius = 4f.dpToPx()
    )

    // bg_my_gift_title
    fun bgGiftTitle(context: Context) = StateListDrawable().apply {
        addState(
            intArrayOf(android.R.attr.state_checked),
            createShapeDrawable(Constant.getPrimaryColor(context), 0f)
        )
        addState(intArrayOf(-android.R.attr.state_checked), null)
    }

    /*
    * LineColor
    * */

    fun bgTransparentDotted10LineColorCorners10(context: Context): GradientDrawable {
        return GradientDrawable().also {
            it.setStroke(SizeHelper.size1, Constant.getLineColor(context), SizeHelper.size10.toFloat(), SizeHelper.size6.toFloat())
            it.cornerRadius = SizeHelper.size10.toFloat()
        }
    }

    fun bgBtnFacebook(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size26.toFloat()
    )

    fun bgGrayF0StrokeLineColor1Corners4(context: Context) = createShapeDrawable(
        color = Color.parseColor("#F0F0F0"),
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size4.toFloat()
    )

    fun bgGrayF0Corners4() = createShapeDrawable(
        color = Color.parseColor("#F0F0F0"),
        radius = SizeHelper.size4.toFloat()
    )


    // bacground app color + stroke lineColor

    fun bgWhiteStrokeLineColor1Corners4(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size4.toFloat()
    )

    fun bgWhiteStrokeLineColor1Corners40(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size40.toFloat()
    )

    fun bgWhiteStrokeLineColor0_5Corners4(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeWidth = SizeHelper.size0_5,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size4.toFloat()
    )

    fun bgWhiteStrokeLineColor0_1(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeWidth = SizeHelper.size0_1,
        strokeColor = Constant.getLineColor(context),
        radius = 0f
    )

    fun bgWhiteStrokeLineColor1(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = 0f
    )


    fun bgGrayF5StrokeLineColor0_5CornersBottom4(context: Context) = createShapeDrawableRadiusBottom(
        color = Color.parseColor("#f5f5f5"),
        strokeColor = Constant.getLineColor(context),
        strokeWidth = SizeHelper.size0_5,
        radiusBottom = SizeHelper.size4.toFloat()
    )

    fun bgTransparentStrokeLineColor1Corners10(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size10.toFloat()
    )

    fun bgTransparentStrokeLineColor1Corners4(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size4.toFloat()
    )

    fun bgTransparentStrokeLineColor1Corners18(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size18.toFloat()
    )

    fun bgTransparentStrokeLineColor1Corners12(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size12.toFloat()
    )

    fun bgTransparentStrokeLineColor0_5Corners4(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size0_5,
        strokeColor = Constant.getLineColor(context),
        radius = SizeHelper.size4.toFloat()
    )

    fun bgTransparentStrokeLineColor0_5(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size0_5,
        strokeColor = Constant.getLineColor(context),
        radius = 0f
    )

    fun bgTransparentStrokeLineColor1(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getLineColor(context),
        radius = 0f,
    )

    fun lineDottedVerticalLineColor(context: Context) = GradientDrawable().apply {
        setStroke(SizeHelper.size1_5, Constant.getLineColor(context), SizeHelper.size1_5.toFloat(), SizeHelper.size5.toFloat())
    }

    fun lineDottedVertical7LineColor(context: Context) = GradientDrawable().apply {
        setStroke(SizeHelper.size7, Constant.getLineColor(context), SizeHelper.size4.toFloat(), SizeHelper.size4.toFloat())
    }


    fun bgEdtSelectorFocusLineColor1(context: Context): StateListDrawable {
        //android:state_focused="true"
        val focused = GradientDrawable().also {
            it.setStroke(SizeHelper.size1, Constant.getPrimaryColor(context))
        }

        val layersFocused = arrayOf(focused)
        val layerDrawbleFocused = LayerDrawable(layersFocused)
        layerDrawbleFocused.setId(0, android.R.id.background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawbleFocused.setLayerInsetStart(0, -SizeHelper.dpToPx(2))
            layerDrawbleFocused.setLayerInsetTop(0, -SizeHelper.dpToPx(2))
            layerDrawbleFocused.setLayerInsetEnd(0, -SizeHelper.dpToPx(2))
            layerDrawbleFocused.setLayerInsetBottom(0, SizeHelper.dpToPx(1))
        }

        //android:state_focused="false"
        val notFocus = GradientDrawable().also {
            it.setStroke(SizeHelper.size1, Constant.getLineColor(context))
        }

        val layersNotFocus = arrayOf(notFocus)
        val layerDrawbleNotFocus = LayerDrawable(layersNotFocus)
        layerDrawbleNotFocus.setId(0, android.R.id.background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawbleNotFocus.setLayerInsetStart(0, -SizeHelper.dpToPx(2))
            layerDrawbleNotFocus.setLayerInsetTop(0, -SizeHelper.dpToPx(2))
            layerDrawbleNotFocus.setLayerInsetEnd(0, -SizeHelper.dpToPx(2))
            layerDrawbleNotFocus.setLayerInsetBottom(0, SizeHelper.dpToPx(1))
        }

        val statesListDrawable = StateListDrawable()
        statesListDrawable.addState(intArrayOf(android.R.attr.state_focused), layerDrawbleFocused)
        statesListDrawable.addState(intArrayOf(-android.R.attr.state_focused), layerDrawbleNotFocus)

        return statesListDrawable
    }

    fun lineUnderColorLine1(context: Context): LayerDrawable {
        //android:state_focused="true"
        val line = GradientDrawable().also {
            it.setStroke(SizeHelper.size1, Constant.getLineColor(context))
            it.setColor(Color.TRANSPARENT)
        }

        val layersFocused = arrayOf(line)
        val layerDrawbleFocused = LayerDrawable(layersFocused)
        layerDrawbleFocused.setId(0, android.R.id.background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawbleFocused.setLayerInsetStart(0, -SizeHelper.dpToPx(10))
            layerDrawbleFocused.setLayerInsetTop(0, -SizeHelper.dpToPx(10))
            layerDrawbleFocused.setLayerInsetEnd(0, -SizeHelper.dpToPx(10))
        }

        return layerDrawbleFocused
    }

    fun btnWhiteStrokeLineColor0_5Corners4(context: Context) = createPressDrawable(
        context,
        createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size1, Constant.getLineColor(context), SizeHelper.size4.toFloat())
    )


    fun bgProductItem(context: Context, left: Int, top: Int, right: Int, bottom: Int): LayerDrawable {
        val backgroundGray = GradientDrawable().also {
            it.setColor(Constant.getLineColor(context))
        }

        val layersFocused = arrayOf(backgroundGray)
        val layerDrawbleFocused = LayerDrawable(layersFocused)
        layerDrawbleFocused.setId(0, android.R.id.background)


        val bgWhite = ContextCompat.getDrawable(context, R.drawable.btn_white)

        val layerList = arrayOf(backgroundGray, bgWhite)
        val layerDrawble = LayerDrawable(layerList)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawble.setLayerInsetStart(1, left)
            layerDrawble.setLayerInsetTop(1, top)
            layerDrawble.setLayerInsetEnd(1, right)
            layerDrawble.setLayerInsetBottom(1, bottom)
        }

        return layerDrawble
    }


    fun setImageColorLineColor(drawable: Drawable, context: Context): Drawable {
        DrawableCompat.setTint(drawable, Constant.getLineColor(context))
        return drawable
    }

    fun setImageColorLineColor(icon: Int, context: Context): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, Constant.getLineColor(context))
        }
        return icon
    }

    fun bgProductItemBottemLeft(context: Context) = bgProductItem(context, left = 0, top = SizeHelper.size1, right = SizeHelper.size0_5, bottom = 0)
    fun bgProductItemBottemRight(context: Context) = bgProductItem(context, left = SizeHelper.size0_5, top = SizeHelper.size1, right = 0, bottom = 0)
    fun bgProductItemTopLeft(context: Context) = bgProductItem(context, left = 0, top = 0, right = SizeHelper.size0_5, bottom = 0)
    fun bgProductItemTopRight(context: Context) = bgProductItem(context, left = SizeHelper.size0_5, top = 0, 0, bottom = 0)


    /*
    * Accent Red
    * */

    fun bgAccentRedCorners4(context: Context) = createShapeDrawable(Constant.getAccentRedColor(context), SizeHelper.size4.toFloat())
    fun bgAccentRedCorners6(context: Context) = createShapeDrawable(Constant.getAccentRedColor(context), SizeHelper.size6.toFloat())
    fun bgAccentRedCorners24(context: Context) = createShapeDrawable(Constant.getAccentRedColor(context), SizeHelper.size24.toFloat())


    fun bgTransparentStrokeAccentRed0_5Corners4(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeColor = Constant.getAccentRedColor(context),
        strokeWidth = SizeHelper.size0_5,
        radius = SizeHelper.size4.toFloat()
    )

    fun bgRedNotifyHome(context: Context): StateListDrawable {
        val notCheck = createShapeDrawable(
            color = Constant.getAccentRedColor(context),
            strokeColor = Color.parseColor("#124998"),
            strokeWidth = SizeHelper.size1_5,
            radius = SizeHelper.size22.toFloat()
        )

        val checked = createShapeDrawable(
            color = Constant.getAccentRedColor(context),
            strokeColor = Color.WHITE,
            strokeWidth = SizeHelper.size1_5,
            radius = SizeHelper.size22.toFloat()
        )

        return createCheckedDrawable(notCheck, checked)
    }

    fun bgRedCircle22dp(context: Context) = ShapeDrawable(OvalShape()).apply {
        paint.color = Constant.getAccentRedColor(context)
    }

    /*
    * Accent Green
    * */

    fun bgAccentGreenCornersLeft45(context: Context) = createShapeDrawableRadiusLeft(Constant.getAccentGreenColor(context), SizeHelper.size45.toFloat())

    fun bgAccentGreenCircleStrokeBlue1_5Size22(context: Context) = createShapeDrawable(
        color = Constant.getAccentGreenColor(context),
        strokeWidth = SizeHelper.size1_5,
        strokeColor = Color.parseColor("#124998"),
        radius = SizeHelper.size22.toFloat()
    )

    fun bgAccentGreenCircleStrokeWhite1_5Size22(context: Context) = createShapeDrawable(
        color = Constant.getAccentGreenColor(context),
        strokeWidth = SizeHelper.size1_5,
        strokeColor = Color.parseColor("#FFFFFF"),
        radius = SizeHelper.size22.toFloat()
    )

    fun bgAccentGreenNotificationHome(context: Context) = createCheckedDrawable(
        bgAccentGreenCircleStrokeBlue1_5Size22(context), bgAccentGreenCircleStrokeWhite1_5Size22(context)
    )

    fun bgAccentGreenCornersTopLeft14(context: Context) = createShapeDrawableRadius(
        floatArrayOf(SizeHelper.size14.toFloat(), SizeHelper.size14.toFloat(), 0f, 0f, 0f, 0f, 0f, 0f),
        Constant.getAccentGreenColor(context)
    )

    fun bgAccentGreenStrokeWhite1Corners22(context: Context) = createShapeDrawable(
        color = Constant.getAccentGreenColor(context),
        strokeWidth = SizeHelper.size1,
        strokeColor = ContextCompat.getColor(context, R.color.white),
        radius = SizeHelper.size22.toFloat()
    )

    fun bgOutlineAccentGreen3Corners4(context: Context) = createShapeDrawable(
        color = Color.TRANSPARENT,
        strokeWidth = SizeHelper.size3,
        strokeColor = Constant.getAccentGreenColor(context),
        radius = SizeHelper.size4.toFloat()
    )


    /*
    * Accent Yellow
    * */

    fun progressbarAccentYellowMission(context: Context): LayerDrawable {
        val background = GradientDrawable().also {
            it.setColor(Color.parseColor("#f0f0f0"))
            it.cornerRadius = SizeHelper.size10.toFloat()
        }


        val progressDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, null).also {
            it.setColor(Constant.getAccentYellowColor(context))
            it.cornerRadius = SizeHelper.size10.toFloat()
        }
        val progress = ScaleDrawable(progressDrawable, Gravity.START, 1f, 0.1f)

        val layers = arrayOf(background, progress)
        val layerDrawble = LayerDrawable(layers)
        layerDrawble.setId(0, android.R.id.background)
        layerDrawble.setId(1, android.R.id.progress)

        return layerDrawble
    }

    fun textColorDisableTextUncheckAccentYellowChecked(context: Context): ColorStateList {
        return createColorStateList(
            Constant.getDisableTextColor(context),
            Constant.getAccentYellowColor(context)
        )
    }


    /*
    *  Accent Cyan
    * */
    fun bgAccentCyanCornersTop8(context: Context) = createShapeDrawableRadiusTop(Constant.getAccentCyanColor(context), SizeHelper.size8.toFloat())

    fun bgAccentCyanCorners4(context: Context) = createShapeDrawable(Constant.getAccentCyanColor(context), SizeHelper.size4.toFloat())


    /*
    *  Normal Text
    * */

    fun textColorNormalCheckedSecondUnchecked(context: Context) = createColorStateList(
        Constant.getSecondTextColor(context),
        Constant.getNormalTextColor(context)
    )

    fun setImageColorNormalText(drawable: Drawable, context: Context): Drawable {
        DrawableCompat.setTint(drawable, Constant.getNormalTextColor(context))
        return drawable
    }

    fun setImageColorNormalText(icon: Int, context: Context): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, Constant.getNormalTextColor(context))
        }
        return icon
    }

    /*
    * Second Text
     */

    fun setImageColorSecondText(drawable: Drawable, context: Context): Drawable {
        DrawableCompat.setTint(drawable, Constant.getSecondTextColor(context))
        return drawable
    }

    fun setImageColorSecondText(icon: Int, context: Context): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, Constant.getSecondTextColor(context))
        }
        return icon
    }


    /*
    * Disable Text
    */

    fun setImageColorDisableText(drawable: Drawable, context: Context): Drawable {
        DrawableCompat.setTint(drawable, Constant.getDisableTextColor(context))
        return drawable
    }

    fun setImageColorDisableText(icon: Int, context: Context): Int {
        ContextCompat.getDrawable(context, icon)?.let {
            DrawableCompat.setTint(it, Constant.getDisableTextColor(context))
        }
        return icon
    }

    fun textColorDisableTextUncheckLightBlueChecked(context: Context): ColorStateList {
        return createColorStateList(
            Constant.getDisableTextColor(context),
            ContextCompat.getColor(context,R.color.light_blue)
        )
    }

    fun textColorDisableTextUncheckYellowChecked(context: Context): ColorStateList {
        return createColorStateList(
            Constant.getDisableTextColor(context),
            ContextCompat.getColor(context,R.color.warning_scan_buy)
        )
    }

    fun textColorDisableTextUncheckViolentChecked(context: Context): ColorStateList {
        return createColorStateList(
            Constant.getDisableTextColor(context),
            ContextCompat.getColor(context,R.color.violet_4)
        )
    }


    /*
    *  App Background White
    * */

    fun bgWhiteCorners4(context: Context) = createShapeDrawable(color = Constant.getAppBackgroundWhiteColor(context), radius = SizeHelper.size4.toFloat())
    fun bgWhiteCorners5(context: Context) = createShapeDrawable(color = Constant.getAppBackgroundWhiteColor(context), radius = SizeHelper.size5.toFloat())
    fun bgWhiteCorners8(context: Context) = createShapeDrawable(color = Constant.getAppBackgroundWhiteColor(context), radius = SizeHelper.size8.toFloat())
    fun bgWhiteCorners10(context: Context) = createShapeDrawable(color = Constant.getAppBackgroundWhiteColor(context), radius = SizeHelper.size10.toFloat())
    fun bgWhiteCorners12(context: Context) = createShapeDrawable(color = Constant.getAppBackgroundWhiteColor(context), radius = SizeHelper.size12.toFloat())
    fun bgWhiteCorners14(context: Context) = createShapeDrawable(color = Constant.getAppBackgroundWhiteColor(context), radius = SizeHelper.size14.toFloat())
    fun bgWhiteCorners16(context: Context) = createShapeDrawable(color = Constant.getAppBackgroundWhiteColor(context), radius = SizeHelper.size16.toFloat())


    fun bgWhiteCornersTop6(context: Context) = createShapeDrawableRadiusTop(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size6.toFloat())
    fun bgWhiteCornersTop10(context: Context) = createShapeDrawableRadiusTop(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size10.toFloat())
    fun bgWhiteCornersTop13(context: Context) = createShapeDrawableRadiusTop(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size13.toFloat())
    fun bgWhiteCornersTop16(context: Context) = createShapeDrawableRadiusTop(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size16.toFloat())
    fun bgWhiteCornersTop20(context: Context) = createShapeDrawableRadiusTop(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size20.toFloat())
    fun bgWhiteCornersTop25(context: Context) = createShapeDrawableRadiusTop(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size25.toFloat())
    fun bgWhiteCornersBottom10(context: Context) = createShapeDrawableRadiusBottom(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size10.toFloat())
    fun bgWhiteCornersLeft20(context: Context) = createShapeDrawableRadiusLeft(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size20.toFloat())


    fun bgWhiteCornersTopLeft6(context: Context) = createShapeDrawableRadius(
        floatArrayOf(SizeHelper.size6.toFloat(), SizeHelper.size6.toFloat(), 0f, 0f, 0f, 0f, 0f, 0f),
        Constant.getAppBackgroundWhiteColor(context)
    )

    fun bgWhiteCornersTopRight6(context: Context) = createShapeDrawableRadius(
        floatArrayOf(0f, 0f, SizeHelper.size6.toFloat(), SizeHelper.size6.toFloat(), 0f, 0f, 0f, 0f),
        Constant.getAppBackgroundWhiteColor(context)
    )

    fun bgProductInfomation(context: Context): GradientDrawable {
        val colors = intArrayOf(Constant.getAppBackgroundWhiteColor(context), Color.parseColor("#97ffffff"), Color.parseColor("#00ffffff"))
        return GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors)
    }

    fun bgWhiteStrokeSecondary1Corners40(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeWidth = SizeHelper.size1,
        strokeColor = Constant.getSecondaryColor(context),
        radius = SizeHelper.size40.toFloat()
    )

    fun bgCircleWhiteStrokeOrange1Size45(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeWidth = SizeHelper.size1,
        strokeColor = ContextCompat.getColor(context, R.color.orange_1),
        radius = SizeHelper.dpToPx(45).toFloat()
    )

    fun bgWhiteStrokeBlue1Corners8(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeColor = Color.parseColor("#3c5a99"),
        strokeWidth = SizeHelper.size1,
        radius = SizeHelper.size8.toFloat()
    )

    fun bgWhiteStrokeSecondary1Corners10(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeColor = Constant.getSecondaryColor(context),
        strokeWidth = SizeHelper.size1,
        radius = SizeHelper.size10.toFloat()
    )

    fun bgWhiteStrokeGray1Corners16(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeColor = Color.parseColor("#f0f0f0"),
        strokeWidth = SizeHelper.size1,
        radius = SizeHelper.size16.toFloat()
    )


    fun bgWhiteStrokeSecondary1Corners4(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeColor = Constant.getSecondaryColor(context),
        strokeWidth = SizeHelper.size1,
        radius = SizeHelper.size4.toFloat()
    )

    fun bgWhiteStrokeGreen1Corners4(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeColor = Constant.getAccentGreenColor(context),
        strokeWidth = SizeHelper.size1,
        radius = SizeHelper.size4.toFloat()
    )

    fun bgWhiteStrokeGrayD4Corners4(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeColor = Color.parseColor("#d4d4d4"),
        strokeWidth = SizeHelper.size1,
        radius = SizeHelper.size4.toFloat()
    )

    fun bgWhiteStrokeGrayD4Corners8(context: Context) = createShapeDrawable(
        color = Constant.getAppBackgroundWhiteColor(context),
        strokeColor = Color.parseColor("#d4d4d4"),
        strokeWidth = SizeHelper.size1,
        radius = SizeHelper.size8.toFloat()
    )


    fun bgCircleWhiteCountCard22dp(context: Context): LayerDrawable {
        val white = GradientDrawable().also {
            it.setSize(SizeHelper.size22, SizeHelper.size22)
            it.cornerRadius = SizeHelper.size22.toFloat()
            it.setColor(Constant.getAppBackgroundWhiteColor(context))
        }

        val green = GradientDrawable().also {
            it.setSize(SizeHelper.size20, SizeHelper.size20)
            it.cornerRadius = SizeHelper.size20.toFloat()
            it.setColor(Constant.getAccentGreenColor(context))
        }


        val layersFocused = arrayOf(white, green)
        val layerDrawbleFocused = LayerDrawable(layersFocused)
        layerDrawbleFocused.setId(0, android.R.id.background)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawbleFocused.setLayerInsetStart(1, SizeHelper.dpToPx(2))
            layerDrawbleFocused.setLayerInsetTop(1, SizeHelper.dpToPx(2))
            layerDrawbleFocused.setLayerInsetEnd(1, SizeHelper.dpToPx(2))
            layerDrawbleFocused.setLayerInsetBottom(1, SizeHelper.dpToPx(2))
        }
        return layerDrawbleFocused
    }


    fun bgWhiteDetailSurveyOption(context: Context): StateListDrawable {
        val bgSecondary = createShapeDrawable(Constant.getSecondaryColor(context), SizeHelper.size5.toFloat())
        val bgWhite = createShapeDrawable(
            Constant.getAppBackgroundWhiteColor(context),
            0, Color.TRANSPARENT,
            0f, SizeHelper.size5.toFloat(),
            SizeHelper.size5.toFloat(), 0f
        )
        val layerList = arrayOf(bgSecondary, bgWhite)
        val layerDrawableChecked = LayerDrawable(layerList)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawableChecked.setLayerInsetLeft(1, SizeHelper.size8)
        }

        val bgWhiteNotChecked = createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size5.toFloat())

        return createCheckedDrawable(bgWhiteNotChecked, layerDrawableChecked)
    }

    fun bgWhiteMyGiftTitle(context: Context): StateListDrawable {
        val bgPrimary = createShapeDrawableRadiusTop(Constant.getPrimaryColor(context), SizeHelper.size4.toFloat())
        val bgWhite = createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), 0f)

        val layerList = arrayOf(bgPrimary, bgWhite)
        val layerDrawableChecked = LayerDrawable(layerList)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layerDrawableChecked.setLayerInsetBottom(1, SizeHelper.size3)
        }

        val bgWhiteNotChecked = createShapeDrawable(Color.TRANSPARENT, 0f)

        return createCheckedDrawable(bgWhiteNotChecked, layerDrawableChecked)
    }


    fun btnWhite(context: Context) = createPressDrawable(
        context,
        createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), 0f)
    )

    fun btnWhiteCorners8(context: Context) = createPressDrawable(
        context,
        createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size8.toFloat())
    )

    fun btnWhiteCornersBottom8(context: Context) = createPressDrawable(
        context,
        createShapeDrawableRadiusBottom(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size8.toFloat())
    )

    fun btnWhiteCornersBottomLeft8(context: Context) = createPressDrawable(
        context,
        createShapeDrawableRadius(
            floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, SizeHelper.size8.toFloat(), SizeHelper.size8.toFloat()),
            Constant.getAppBackgroundWhiteColor(context)
        )
    )

    fun btnWhiteCornersBottomRight8(context: Context) = createPressDrawable(
        context,
        createShapeDrawableRadius(
            floatArrayOf(0f, 0f, 0f, 0f, SizeHelper.size8.toFloat(), SizeHelper.size8.toFloat(), 0f, 0f),
            Constant.getAppBackgroundWhiteColor(context)
        )
    )

    fun btnWhiteStroke1Corners36(context: Context) = createPressDrawable(
        context,
        createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size1, Constant.getSecondaryColor(context), SizeHelper.size36.toFloat())
    )

    fun btnWhiteStrokeSecondary1Corners4(context: Context) = createPressDrawable(
        context,
        createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size1, Constant.getSecondaryColor(context), SizeHelper.size4.toFloat())
    )

    fun btnWhiteStrokePrimary1Corners4(context: Context) = createPressDrawable(
        context,
        createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), SizeHelper.size1, Constant.getPrimaryColor(context), SizeHelper.size4.toFloat())
    )

    fun btnWhiteCircle48dp(context: Context) = createPressDrawable(
        context, createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), SizeHelper.dpToPx(48).toFloat())
    )

    fun progressWhiteBackgroundGrayCorners4(context: Context): LayerDrawable {
        val radius = 4f.dpToPx()
        return createProgressStateListDrawable(
            backgroundDrawable = createShapeDrawable(Color.parseColor("#80FFFFFF"), radius),
            secondaryDrawable = createScaleDrawable(createShapeDrawable(Color.TRANSPARENT, radius)),
            progressDrawable = createScaleDrawable(createShapeDrawable(Constant.getAppBackgroundWhiteColor(context), radius))
        )
    }

    /*
    *  END App Background White
    * */


    /*
    *  App Background GRAY
    * */

    fun bgGrayCorners4(context: Context) = createShapeDrawable(ContextCompat.getColor(context,R.color.grayF0), SizeHelper.size4.toFloat())
    fun bgGrayCorners10(context: Context) = createShapeDrawable(ContextCompat.getColor(context,R.color.grayF0), SizeHelper.size10.toFloat())
    fun bgGrayCorners32(context: Context) = createShapeDrawable(ContextCompat.getColor(context,R.color.grayF0), SizeHelper.size32.toFloat())


    fun bgGrayCornersTopRight12BottomRight12(context: Context) = createShapeDrawableRadius(
        floatArrayOf(0f, 0f, SizeHelper.size12.toFloat(), SizeHelper.size12.toFloat(), SizeHelper.size12.toFloat(), SizeHelper.size12.toFloat(), 0f, 0f),
        ContextCompat.getColor(context,R.color.grayF0)
    )

    fun btnSwitchGrayUncheckedGreenCheckedWidth50Height30(context: Context): StateListDrawable {
        val bgChecked = createShapeDrawable(ContextCompat.getColor(context, R.color.green3), 0, Color.TRANSPARENT, SizeHelper.size30.toFloat())
        val bgUnchecked = createShapeDrawable(ContextCompat.getColor(context,R.color.grayF0), 0, Color.TRANSPARENT, SizeHelper.size30.toFloat())

        return createCheckedDrawable(bgUnchecked, bgChecked)
    }

    fun progressGrayBackgroundColorLineCorners10(context: Context): LayerDrawable {
        val radius = 10f.dpToPx()
        val progressColor = ContextCompat.getColor(context, R.color.grayD8)
        return createProgressStateListDrawable(
            backgroundDrawable = createShapeDrawable(ContextCompat.getColor(context,R.color.grayF0), radius),
            secondaryDrawable = createScaleDrawable(createShapeDrawable(progressColor, radius)),
            progressDrawable = createScaleDrawable(createShapeDrawable(progressColor, radius))
        )
    }


    /*
    *  App Background Popup
    * */

    fun bgPopupCorners4(context: Context) = createShapeDrawable(Constant.getBackgroundPopupColor(context), SizeHelper.size4.toFloat())
    fun bgPopupCorners10(context: Context) = createShapeDrawable(Constant.getBackgroundPopupColor(context), SizeHelper.size10.toFloat())


}
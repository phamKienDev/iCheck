package vn.icheck.android.component.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import vn.icheck.android.R

class CustomButton : AppCompatTextView {

    constructor(context: Context) : super(context) {
        setupView(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupView(attrs)
    }

    private fun setupView(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton, 0, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                !typedArray.hasValue(R.styleable.CustomButton_cb_pressed_background_color) &&
                !typedArray.hasValue(R.styleable.CustomButton_cb_pressed_stroke_color)) {
            setBackgroundRipple(typedArray)
        } else {
            setBackgroundGradient(typedArray)
        }
    }

    @RequiresApi(21)
    private fun setBackgroundRipple(typedArray: TypedArray) {
        val statesListDrawable = StateListDrawable()

        val strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CustomButton_cb_stroke_width, 0)
        val radius  = typedArray.getDimensionPixelSize(R.styleable.CustomButton_cb_radius, 0).toFloat()

        if (typedArray.hasValue(R.styleable.CustomButton_cb_disable_background_color)) {
            statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled),
                    ViewHelper.createShapeDrawable(
                            typedArray.getColor(R.styleable.CustomButton_cb_disable_background_color, ContextCompat.getColor(context, R.color.colorLineView)),
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_disable_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        } else if (typedArray.hasValue(R.styleable.CustomButton_cb_disable_stroke_color)) {
            statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled),
                    ViewHelper.createShapeDrawable(
                            ContextCompat.getColor(context, R.color.colorLineView),
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_disable_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        }

        if (typedArray.hasValue(R.styleable.CustomButton_cb_enable_background_color)) {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled),
                    ViewHelper.createRippleDrawable(
                            typedArray.getColor(R.styleable.CustomButton_cb_enable_background_color, Color.TRANSPARENT),
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_enable_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        } else if (typedArray.hasValue(R.styleable.CustomButton_cb_enable_stroke_color)) {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled),
                    ViewHelper.createRippleDrawable(
                            Color.TRANSPARENT,
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_enable_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        }

        background = statesListDrawable
    }

    private fun setBackgroundGradient(typedArray: TypedArray) {
        val statesListDrawable = StateListDrawable()

        val strokeWidth = typedArray.getDimensionPixelSize(R.styleable.CustomButton_cb_stroke_width, 0)
        val radius  = typedArray.getDimensionPixelSize(R.styleable.CustomButton_cb_radius, 0).toFloat()

        if (typedArray.hasValue(R.styleable.CustomButton_cb_disable_background_color)) {
            statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled),
                    ViewHelper.createShapeDrawable(
                            typedArray.getColor(R.styleable.CustomButton_cb_disable_background_color, Color.TRANSPARENT),
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_disable_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        } else if (typedArray.hasValue(R.styleable.CustomButton_cb_disable_stroke_color)) {
            statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled),
                    ViewHelper.createShapeDrawable(
                            Color.TRANSPARENT,
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_disable_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        }

        if (typedArray.hasValue(R.styleable.CustomButton_cb_pressed_background_color)) {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_pressed),
                    ViewHelper.createShapeDrawable(
                            typedArray.getColor(R.styleable.CustomButton_cb_pressed_background_color, Color.TRANSPARENT),
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_pressed_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        } else if (typedArray.hasValue(R.styleable.CustomButton_cb_pressed_stroke_color)) {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_pressed),
                    ViewHelper.createShapeDrawable(
                            Color.TRANSPARENT,
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_pressed_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        }

        if (typedArray.hasValue(R.styleable.CustomButton_cb_enable_background_color)) {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled),
                    ViewHelper.createShapeDrawable(
                            typedArray.getColor(R.styleable.CustomButton_cb_enable_background_color, Color.TRANSPARENT),
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_enable_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        } else if (typedArray.hasValue(R.styleable.CustomButton_cb_enable_stroke_color)) {
            statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled),
                    ViewHelper.createShapeDrawable(
                            Color.TRANSPARENT,
                            strokeWidth,
                            typedArray.getColor(R.styleable.CustomButton_cb_enable_stroke_color, Color.TRANSPARENT),
                            radius)
            )
        }

        background = statesListDrawable
    }
}
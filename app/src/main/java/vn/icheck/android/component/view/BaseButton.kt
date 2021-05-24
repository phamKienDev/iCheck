package vn.icheck.android.component.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICButton

abstract class BaseButton : AppCompatTextView {
    /**
     * Size mặc định của text là 16sp
     */
    protected var defaultTextColor: ColorStateList? = null
    protected var defaultTextSize = SizeHelper.size16sp

    protected var defaultDisableBackground = Color.TRANSPARENT
    protected var defaultEnableBackground = Color.TRANSPARENT
    protected var defaultPressedBackground = Color.TRANSPARENT

    protected var defaultStrokeWidth = 0
    protected var defaultStrokeColor = Color.TRANSPARENT
    protected var defaultRadius = 0F

    constructor(context: Context) : super(context) {
        onSetupDefault()
        setupView(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        onSetupDefault()
        setupView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        onSetupDefault()
        setupView(attrs)
    }

    protected abstract fun onSetupDefault()

    private fun setupView(attrs: AttributeSet?) {
        gravity = Gravity.CENTER
//        typeface = ViewHelper.createTypeface()
        includeFontPadding = false

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseButton, 0, 0)

        if (typedArray.hasValue(R.styleable.BaseButton_buttonTextColor)) {
            defaultTextColor = typedArray.getColorStateList(R.styleable.BaseButton_buttonTextColor)
        }
        setTextColor(defaultTextColor)

        if (typedArray.hasValue(R.styleable.BaseButton_buttonTextSize)) {
            defaultTextSize = typedArray.getDimensionPixelSize(R.styleable.BaseButton_buttonTextSize, SizeHelper.size16sp.toInt()).toFloat()
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize)

        if (typedArray.hasValue(R.styleable.BaseButton_buttonStrokeWidth)) {
            defaultStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.BaseButton_buttonStrokeWidth, SizeHelper.size1)
        }

        if (typedArray.hasValue(R.styleable.BaseButton_buttonStrokeColor)) {
            defaultStrokeColor = typedArray.getColor(R.styleable.BaseButton_buttonStrokeColor, vn.icheck.android.ichecklibs.Constant.getPrimaryColor(context))
        }

        if (typedArray.hasValue(R.styleable.BaseButton_buttonRadius)) {
            defaultRadius = typedArray.getDimensionPixelSize(R.styleable.BaseButton_buttonRadius, SizeHelper.size4).toFloat()
        }

        background = ViewHelper.createStateListDrawable(
                defaultDisableBackground, defaultEnableBackground, defaultPressedBackground,
                defaultStrokeColor, defaultStrokeColor, defaultStrokeColor,
                defaultStrokeWidth, defaultRadius
        )
    }

    /*
    * Yêu cầu file background trong drawable phải sắp xếp items theo thứ tự sau:
    * 1. Màu của button phải để dưới cùng
    * 2. Màu của trạng thái pressed phải để vị trí thứ 2 từ dưới lên (API dưới 21)
    * */
    open fun setTheme(obj: ICButton) {
        setTextColor(Color.parseColor(obj.color))
        text = obj.label
        tag = obj.schema

        if (!obj.backgroundImage.isNullOrEmpty()) {
            changeBackgroundImage(obj.backgroundImage!!, obj.backgroundColor, obj.backgroundHover)
        } else {
            changeBackgroundColor(obj.backgroundColor, obj.backgroundHover)
        }
    }

    private fun changeBackgroundImage(image: String, color: String?, hover: String?) {
        Glide.with(this.context.applicationContext)
                .asDrawable()
                .load(image)
                .transform(RoundedCorners(defaultRadius.toInt()))
                .into(object : CustomTarget<Drawable>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)

                        changeBackgroundColor(color, hover)
                    }

                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        if (this@BaseButton != null) {
                            val statesListDrawable = StateListDrawable()

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled), ViewHelper.createRippleDrawable(context, resource))
                                statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled),
                                        ViewHelper.createShapeDrawable(ContextCompat.getColor(context, R.color.grayLoyalty), defaultRadius)
                                )
                            } else {
                                statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled), resource)
                            }

                            background = statesListDrawable
                        }
                    }
                })
    }

    private fun changeBackgroundColor(color: String?, hover: String?) {
        if (color != null && hover != null) {
            val statesListDrawable = StateListDrawable()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled), ViewHelper.createRippleDrawable(Color.parseColor(color), defaultStrokeWidth, defaultStrokeColor, defaultRadius))
                statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), ViewHelper.createShapeDrawable(ContextCompat.getColor(context, R.color.colorLineView), defaultStrokeWidth, defaultStrokeColor, defaultRadius))
            } else {
                statesListDrawable.addState(intArrayOf(android.R.attr.state_enabled), ViewHelper.createShapeDrawable(Color.parseColor(color), defaultStrokeWidth, defaultStrokeColor, defaultRadius))
                statesListDrawable.addState(intArrayOf(android.R.attr.state_pressed), ViewHelper.createShapeDrawable(Color.parseColor(hover), defaultStrokeWidth, defaultStrokeColor, defaultRadius))
                statesListDrawable.addState(intArrayOf(-android.R.attr.state_enabled), ViewHelper.createShapeDrawable(ContextCompat.getColor(context, R.color.colorLineView), defaultStrokeWidth, defaultStrokeColor, defaultRadius))
            }

            background = ViewHelper.createStateListDrawable(
                    ContextCompat.getColor(context, R.color.colorLineView), Color.parseColor(color), Color.parseColor(hover),
                    defaultStrokeColor, defaultStrokeColor, defaultStrokeColor,
                    defaultStrokeWidth, defaultRadius
            )
        }
    }

    fun setOnClickListener(listener: OnClickListener) {
        setOnClickListener {
            if (tag != null && tag is String) {
                listener.onClicked(tag as String)
            } else {
                listener.onClicked(null)
            }
        }
    }

    interface OnClickListener {
        fun onClicked(schema: String?)
    }
}
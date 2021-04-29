package vn.icheck.android.ui.edittext

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.InputType.*
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import vn.icheck.android.R
import vn.icheck.android.util.ick.toPx


class FocusableEditText : AppCompatEditText {
    var mErrorDrawable: Drawable? = null
    var mError: CharSequence? = null
    lateinit var mErrorPaint: Paint
    lateinit var mErrorTextPaint: Paint
    lateinit var mLinePaint: Paint

    private val drawableClear = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_delete_gray_vector, null)

    private val drawableEyeOff = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_eye_off_gray_24dp, null)

    private val drawableEye = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_eye_on_vector, null)

    //    private val currentHint = hint
    private var currentText = ""
    private var leftDrawable: Drawable? = null
    private var rightDrawable: Drawable? = null
    var originalPadding = 0
    var enableRightClick = true

    constructor(context: Context) : super(context) {
        initFont()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initFont()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, R.style.FocusableEditTextTheme) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.AppCompatTextView, 0, 0)
        initFont()
    }

    private fun initFont() {
        typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        if (inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD || inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            if (transformationMethod == null) {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
            }

        }
        mErrorDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_error_red_18dp, null)
        mErrorPaint = Paint()
        mErrorTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mErrorTextPaint.textSize = 12 * getResources().getDisplayMetrics().scaledDensity
        mErrorTextPaint.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        mLinePaint = Paint()
        mLinePaint.strokeWidth = 1f.toPx()

        mErrorTextPaint.color = ContextCompat.getColor(context, R.color.colorAccentRed)
        setBackgroundResource(0)
        originalPadding = paddingBottom
    }


    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        setDrawableFocusable()
    }

    override fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        super.setCompoundDrawables(left, top, right, bottom)
        leftDrawable = left
        if (right?.constantState?.equals(drawableClear?.constantState) == false) {
            rightDrawable = right
        }
    }

//    override fun setCompoundDrawablesWithIntrinsicBounds(left: Int, top: Int, right: Int, bottom: Int) {
//        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
//        leftDrawable = ResourcesCompat.getDrawable(resources, left, null)
//        rightDrawable = ResourcesCompat.getDrawable(resources, right, null)
//    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        currentText = text.toString()
        setDrawableFocusable()
        mError = null
        if (!text.isNullOrEmpty() && originalPadding != paddingBottom) {
            setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
            requestLayout()
        }
    }

    private fun setDrawableFocusable() {
        if (rightDrawable == null) {
            if (currentText.isNotEmpty() && isFocused) {
                if (inputType != TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD && inputType != TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable
                            ?: drawableClear, null)
                } else {
                    if (transformationMethod == null) {
                        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
                    }

                }
            } else {
                if (currentText.isNotEmpty()) {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableClear, null)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null)
                }
            }
        } else {
            if (inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD || inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                if (transformationMethod == null) {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
                }

            }else {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP && enableRightClick) {
            if (event.rawX > right - totalPaddingRight) {
                if (inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD || inputType == TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD) {
                    transformationMethod = if (transformationMethod == null) {
                        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
                        PasswordTransformationMethod()
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
                        null
                    }
                } else {
                    setText("")
                }
                performClick()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        requestFocus()
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            canvas?.translate(scrollX.toFloat(), 0f)
            val bottom = height - paddingBottom + 2.5f.toPx()
            if (!mError.isNullOrEmpty()) {
                mLinePaint.setColor(ContextCompat.getColor(context, R.color.colorAccentRed))
                drawLine(0f, bottom.toFloat(), width.toFloat(), bottom.toFloat(), mLinePaint)
                drawBitmap(
                        mErrorDrawable!!.toBitmap(),
                        0f,
                        bottom.toFloat(),
                        mErrorPaint
                )
                drawText(
                        mError.toString(),
                        26.toPx().toFloat(),
                        (bottom + 15f.toPx()).toFloat(),
                        mErrorTextPaint
                )
            } else {
                if (hasFocus()) {
                    mLinePaint.setColor(Color.parseColor("#057DDA"))
                } else {
                    mLinePaint.setColor(Color.parseColor("#D8D8D8"))
                }
                drawLine(0f, bottom.toFloat(), width.toFloat(), bottom.toFloat(), mLinePaint)
            }
            canvas?.translate(0f, 0f)
        }
    }

    override fun setError(error: CharSequence?) {
        mError = error
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
        requestLayout()
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + 20.toPx())
        requestLayout()
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        mError = error
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
        requestLayout()
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + 20.toPx())
        requestLayout()
    }
}
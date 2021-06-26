package vn.icheck.android.ichecklibs.view.primary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputType.*
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.marginBottom
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.R
import vn.icheck.android.ichecklibs.SizeHelper
import vn.icheck.android.ichecklibs.util.dpToPx
import vn.icheck.android.ichecklibs.util.toPx

class FocusableEdittextLineColorUncheckPrimaryChecked : AppCompatEditText {

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
    private var isPassword: Boolean? = null

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

    private val isSetDrawable: Boolean
        get() {
            if (isPassword == null) {
                isPassword = when (inputType) {
                    TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD -> true
                    TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_VISIBLE_PASSWORD -> true
                    TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD -> true
                    TYPE_TEXT_VARIATION_PASSWORD -> true
                    else -> {
                        false
                    }
                }
            }

            return isPassword ?: false
        }

    private fun initFont() {
        typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")

        setHintTextColor(ColorManager.getDisableTextColor(context))
        setTextColor(ColorManager.getNormalTextColor(context))

        if (isSetDrawable) {
            if (transformationMethod == null) {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
            }
        }

        mErrorDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_error_red_18dp, null)
        mErrorPaint = Paint()
        mErrorTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mErrorTextPaint.textSize = 12 * resources.displayMetrics.scaledDensity
        mErrorTextPaint.typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        mLinePaint = Paint()
        mLinePaint.strokeWidth = 1f.toPx()

        mErrorTextPaint.color = ColorManager.getAccentRedColor(context)
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

        if (!text.isNullOrEmpty() && originalPadding != paddingBottom) {
            setPadding(paddingLeft, paddingTop, paddingRight, originalPadding)
            requestLayout()
        }

        checkCenterViewMarginBottom()
        if (originalPadding != paddingBottom) {
            centerView?.let { view ->
                (view.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = centerViewMarginBottom + SizeHelper.size20
            }
        }else {
            centerView?.let { view ->
                (view.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = centerViewMarginBottom
            }
        }

        mError = null
    }

    private fun setDrawableFocusable() {
        if (rightDrawable == null) {
            if (currentText.isNotEmpty() && isFocused) {
                if (!isSetDrawable) {
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
            if (isSetDrawable) {
                if (transformationMethod == null) {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
                }

            } else {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null)
            }
        }
    }

    private var centerView: View? = null
    private var centerViewMarginBottom = 0

    fun setCenterView(view: View) {
        centerView = view
        centerViewMarginBottom = view.marginBottom
    }

    fun getBottomPadding(): Int {
        return if (originalPadding != paddingBottom) {
            SizeHelper.size32
        } else {
            SizeHelper.size12
        }
    }

    private fun checkCenterViewMarginBottom() {
        if (centerViewMarginBottom == 0) {
            centerViewMarginBottom = centerView?.marginBottom ?: 0
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP && enableRightClick) {
            if (event.rawX > right - totalPaddingRight) {
                if (isSetDrawable) {
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
            canvas.translate(scrollX.toFloat(), 0f)
            val bottom = height - paddingBottom + 2.5f.toPx()

            if (!mError.isNullOrEmpty()) {
                mLinePaint.color = ContextCompat.getColor(context, R.color.colorAccentRed)
                drawLine(0f, bottom, width.toFloat(), bottom, mLinePaint)
                drawBitmap(mErrorDrawable!!.toBitmap(), 0f, bottom, mErrorPaint)
                drawText(mError.toString(), 26.dpToPx().toFloat(), (bottom + 15f.toPx()), mErrorTextPaint)
            } else {
                if (hasFocus()) {
                    mLinePaint.color = ColorManager.getPrimaryColor(context)
                } else {
                    mLinePaint.color = ColorManager.getLineColor(context)
                }
                drawLine(0f, bottom, width.toFloat(), bottom, mLinePaint)

            }

            canvas.translate(0f, 0f)
        }
    }

    override fun setError(error: CharSequence?) {
        mError = error

//        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
//        requestLayout()
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding + 20.dpToPx())
        requestLayout()

        checkCenterViewMarginBottom()
        centerView?.let { view ->
            (view.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = centerViewMarginBottom + SizeHelper.size20
        }
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        mError = error

//        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
//        requestLayout()
        setPadding(paddingLeft, paddingTop, paddingRight, originalPadding + 20.dpToPx())
        requestLayout()

        checkCenterViewMarginBottom()
        centerView?.let { view ->
            (view.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = centerViewMarginBottom + SizeHelper.size20
        }
    }
}
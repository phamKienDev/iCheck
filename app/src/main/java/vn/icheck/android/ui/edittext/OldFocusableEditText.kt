package vn.icheck.android.ui.edittext

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import vn.icheck.android.R

class OldFocusableEditText: AppCompatEditText {
    constructor(context: Context):super(context){
        initFont()
    }

    constructor(context: Context, attrs: AttributeSet):super(context, attrs){
        initFont()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int):super(context, attrs, R.style.FocusableEditTextTheme){
        context.theme.obtainStyledAttributes(attrs, R.styleable.AppCompatTextView, 0, 0)
        initFont()
    }

    private fun initFont() {
        typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
        if (inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD || inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            if (transformationMethod == null) {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
            }

        }
        if (background != null) {
            setBackgroundResource(R.drawable.edt_bg_selector)
        }
    }

    private val drawableClear =  ResourcesCompat.getDrawable(context.resources, R.drawable.ic_delete_gray_vector, null)

    private val drawableEyeOff =  ResourcesCompat.getDrawable(context.resources, R.drawable.ic_eye_off_gray_24dp, null)

    private val drawableEye  =  ResourcesCompat.getDrawable(context.resources, R.drawable.ic_eye_on_vector, null)

    //    private val currentHint = hint
    private var currentText = ""
    private var leftDrawable: Drawable? = null
    private var rightDrawable: Drawable? = null

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
//        hint = if (focused) {
//            ""
//        } else {
//            currentHint
//        }
        setDrawableFocusable()
    }

    override fun setCompoundDrawables(left: Drawable?, top: Drawable?, right: Drawable?, bottom: Drawable?) {
        super.setCompoundDrawables(left, top, right, bottom)
        leftDrawable = left
        rightDrawable = right
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        currentText = text.toString()
        setDrawableFocusable()
    }

    private fun setDrawableFocusable() {
        if (rightDrawable == null) {
            if (currentText.isNotEmpty() && isFocused) {
                if (inputType != InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD && inputType != InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableClear, null)
                } else {
                    if (transformationMethod == null) {
                        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
                    }

                }
            } else {
                if (currentText.isNotEmpty()) {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }
            }
        } else {
            if (inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD || inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                if (transformationMethod == null) {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEyeOff, null)
                } else {
                    setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, drawableEye, null)
                }

            }else if (currentText.isEmpty()) {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (event.rawX > right - totalPaddingRight) {
                if (inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD || inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
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
}
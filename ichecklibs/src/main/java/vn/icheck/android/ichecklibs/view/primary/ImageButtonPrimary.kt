package vn.icheck.android.ichecklibs.view.primary

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.graphics.drawable.DrawableCompat
import vn.icheck.android.ichecklibs.Constant

class ImageButtonPrimary:AppCompatImageButton {
    constructor(context: Context) : super(context) { setup() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { setup() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { setup() }

    fun setup(){
        val outValue=TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground,outValue,true)
        setBackgroundResource(outValue.resourceId)

        drawable?.let { DrawableCompat.setTint(it,Constant.getPrimaryColor(context)) }
        setImageDrawable(drawable)
    }
}
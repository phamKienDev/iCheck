package vn.icheck.android.component.view

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

open class MySpannable(isUnderline: Boolean,val color:String) : ClickableSpan() {
    private var isUnderline = false

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
        ds.color = Color.parseColor(color)
    }

    override fun onClick(widget: View) {}

    /**
     * Constructor
     */
    init {
        this.isUnderline = isUnderline
    }
}
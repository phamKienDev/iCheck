package vn.icheck.android.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RatioImageView : AppCompatImageView {
    var widthPercent = 1f
    var heightPercent = 1f

    constructor(context: Context, width: Float, height: Float) : super(context) {
        widthPercent = width
        heightPercent = height
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, ((measuredWidth / widthPercent) * heightPercent).toInt())
    }
}
package vn.icheck.android.component.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class DemoView : LinearLayout {

    constructor(context: Context) : this(context, null) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupView()
    }

    private fun setupView() {
        if (childCount == 0) {
            addView(ViewHelper.createFeed(context))
        }
    }
}
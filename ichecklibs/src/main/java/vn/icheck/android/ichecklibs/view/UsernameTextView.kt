package vn.icheck.android.ichecklibs.view

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class UsernameTextView: AppCompatTextView {
    var textWidth = 0f
    var maxCharPerLine = 1
    var isVerified = false

    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    fun initView() {
        maxLines = 2
        val mp = Paint()
        val rect = Rect()
        val text = "H"
        mp.getTextBounds(text, 0, text.length, rect)
//        if (rect.width() > 0) {
//            textWidth = rect.width().toFloat() + 1f
//        } else {
//            textWidth = 10f
//        }
        textWidth = mp.measureText(text,0,1)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

    }
}
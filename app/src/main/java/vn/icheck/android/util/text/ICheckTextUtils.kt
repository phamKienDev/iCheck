package vn.icheck.android.util.text

import android.graphics.Paint
import android.view.View
import android.widget.TextView

class ICheckTextUtils {
    companion object{
        fun setSalePrice(textView: TextView?, price: Long) {
            textView?.text = String.format("%,dđ", price)
            textView?.paintFlags = textView?.paintFlags!! or Paint.STRIKE_THRU_TEXT_FLAG
            if (price > 0L) {
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
            }
        }

        fun setPrice(textView: TextView?, price: Long) {
            textView?.text = String.format("%,dđ", price)
        }
    }
}
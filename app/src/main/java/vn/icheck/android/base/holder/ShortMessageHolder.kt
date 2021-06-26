package vn.icheck.android.base.holder

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper

class ShortMessageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createShortMessage(parent.context)) {

    fun bind(icon: Int?, message: String?, buttonID: Int? = null) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as AppCompatImageView).run {
                setImageResource(icon ?: R.drawable.ic_error_request)
            }

            (getChildAt(1) as AppCompatTextView).run {
                text = message
            }

            (getChildAt(2) as AppCompatTextView).run {
                if (buttonID != null) {
                    visibility = if (buttonID > 0) {
                        setText(buttonID)
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                } else {
                    visibility = View.VISIBLE
                    setText(R.string.thu_lai)
                }
            }
        }
    }

    fun setListener(listener: View.OnClickListener?) {
        ((itemView as ViewGroup).getChildAt(2) as AppCompatTextView).setOnClickListener {
            listener?.onClick(it)
        }
    }
}
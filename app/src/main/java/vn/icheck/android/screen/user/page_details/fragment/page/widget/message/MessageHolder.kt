package vn.icheck.android.screen.user.page_details.fragment.page.widget.message

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class MessageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createMessage(parent.context)) {

    fun bind(icon: Int?, title: String? = null, message: String? = null, buttonID: Int? = null) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as AppCompatImageView).run {
                setImageResource(icon ?: R.drawable.ic_empty_product_110dp)
            }

            (getChildAt(1) as AppCompatTextView).run {
                if (title.isNullOrEmpty()) {
                    beGone()
                } else {
                    beVisible()
                    text = title
                }
            }

            (getChildAt(2) as AppCompatTextView).run {
                if (message.isNullOrEmpty()) {
                    beGone()
                } else {
                    beVisible()
                    text = message
                }
            }

            (getChildAt(3) as AppCompatTextView).run {
                if (buttonID != null) {
                    if (buttonID != -1) {
                        beVisible()
                        setText(buttonID)
                    } else {
                        beGone()
                    }
                } else {
                    setText(R.string.thu_lai)
                }
            }
        }
    }

    fun listener(listener: View.OnClickListener?) {
        ((itemView as ViewGroup).getChildAt(3) as AppCompatTextView).run {
            if (listener != null) {
                visibility = View.VISIBLE

                setOnClickListener {
                    listener.onClick(it)
                }
            } else {
                visibility = View.GONE
            }
        }
    }
}
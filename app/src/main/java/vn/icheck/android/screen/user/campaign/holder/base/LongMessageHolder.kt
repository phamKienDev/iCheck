package vn.icheck.android.screen.user.campaign.holder.base

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper

class LongMessageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(ViewHelper.createLongMessage(parent.context)) {

    fun bind(icon: Int?, message: String?, buttonID: Int?) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as LinearLayout).run {
                (getChildAt(0) as AppCompatImageView).run {
                    setImageResource(icon ?: R.drawable.ic_empty_notifi_243x138)
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
    }

    fun setListener(listener: View.OnClickListener?) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as LinearLayout).run {
                getChildAt(2).setOnClickListener {
                    listener?.onClick(it)
                }
            }
        }
    }
}
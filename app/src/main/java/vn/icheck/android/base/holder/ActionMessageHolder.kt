package vn.icheck.android.base.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_action_message_holder.view.*
import vn.icheck.android.R

class ActionMessageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_action_message_holder, parent, false)) {

    fun bind(image: Int?, title: String?, message: String?, buttonName: Int? = null) {
        itemView.imgAction.setImageResource(image ?: R.drawable.ic_group_120dp)

        if (!title.isNullOrEmpty()) {
            itemView.tvTitle.visibility = View.VISIBLE
            itemView.tvTitle.text = title
        } else {
            itemView.tvTitle.visibility = View.GONE
        }

        if (!message.isNullOrEmpty()) {
            itemView.tvMessage.visibility = View.VISIBLE
            itemView.tvMessage.text = message
        } else {
            itemView.tvMessage.visibility = View.GONE
        }

        if (buttonName != null) {
            itemView.tvAction.visibility = View.VISIBLE
            itemView.tvAction.text = itemView.context.getString(buttonName)
        } else {
            itemView.tvAction.visibility = View.GONE
        }

    }

    fun setListener(click: View.OnClickListener?) {
        itemView.tvAction.setOnClickListener {
            click?.onClick(it)
        }
    }
}
package vn.icheck.android.activities.product.review_product_v1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.R

class MessageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)) {

    fun bind(icon: Int, message: String, button: Int? = null) {
        itemView.imgIcon.setImageResource(icon)

        itemView.txtMessage.text = message

        if (button != null) {
            itemView.btnTryAgain.visibility = View.VISIBLE
            itemView.btnTryAgain.setText(button)
        } else {
            itemView.btnTryAgain.visibility = View.INVISIBLE
        }
    }

    fun listener(listener: View.OnClickListener) {
        itemView.btnTryAgain.setOnClickListener {
            listener.onClick(it)
        }
    }
}
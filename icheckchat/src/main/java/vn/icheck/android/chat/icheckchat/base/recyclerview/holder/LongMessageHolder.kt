package vn.icheck.android.chat.icheckchat.base.recyclerview.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.view.setGone
import vn.icheck.android.chat.icheckchat.base.view.setInvisible
import vn.icheck.android.chat.icheckchat.base.view.setVisible

class LongMessageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_chat, parent, false)) {

    fun bind(imgError: Int, msgError: String, msgErrorBottom: String?, msgButton: String?, bgButton: Int, colorButton: Int, callback: View.OnClickListener? = null) {
        itemView.findViewById<AppCompatImageView>(R.id.imgDefault).setImageResource(imgError)

        itemView.findViewById<AppCompatTextView>(R.id.tvMessageTop).apply {
            text = if (msgError.isEmpty()) {
                ""
            } else {
                msgError
            }
        }

        itemView.findViewById<AppCompatTextView>(R.id.tvMessageBottom).apply {
            if (msgErrorBottom.isNullOrEmpty()) {
                setGone()
            } else {
                setVisible()
                text = msgErrorBottom
            }
        }

        itemView.findViewById<AppCompatTextView>(R.id.btnDefault).apply {
            setBackgroundResource(bgButton)

            if (msgButton.isNullOrEmpty()) {
                setInvisible()
            } else {
                setVisible()
                text = msgButton
                setTextColor(ContextCompat.getColor(itemView.context, colorButton))
            }
            setOnClickListener(callback)
        }
    }
}
package vn.icheck.android.screen.user.scan_history.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_scan_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError

class MessageScanHistoryHolder (parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_scan_holder, parent, false)) {
    fun bind(item: ICError) {
        itemView.imgAction.setImageResource(item.icon)
        itemView.tvMessage.text = item.subMessage
    }
}
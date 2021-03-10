package vn.icheck.android.component.postofuser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_qa_outgoing_message.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.post.ICQAInPost

class QAOutgoingMessageViewHolder (view: View) : BaseViewHolder<ICQAInPost>(view) {

    override fun bind(obj: ICQAInPost) {
        itemView.tvMessage.text = obj.msg
    }

    companion object {
        fun createHolder(parent: ViewGroup): QAOutgoingMessageViewHolder {
            return QAOutgoingMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_qa_outgoing_message, parent, false))
        }
    }
}
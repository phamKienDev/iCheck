package vn.icheck.android.component.postofuser

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.layout_qa_incoming_message.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.post.ICQAInPost

class QAIncomingMessageViewHolder (view: View) : BaseViewHolder<ICQAInPost>(view) {

    override fun bind(obj: ICQAInPost) {
        itemView.tvMessage.text = obj.msg
        ViewHelper.makeTextViewResizable(itemView.tvMessage,3, itemView.tvMessage.context.getString(R.string.xem_them),true, vn.icheck.android.ichecklibs.ColorManager.getSecondaryColorCode(itemView.context))
    }

    companion object {
        fun createHolder(parent: ViewGroup): QAIncomingMessageViewHolder {
            return QAIncomingMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_qa_incoming_message, parent, false))
        }
    }
}
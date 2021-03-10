package vn.icheck.android.component.privacy_post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_privacy_post.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICPrivacy

class PrivacyPostAdapter : RecyclerViewAdapter<ICPrivacy>() {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICPrivacy>(LayoutInflater.from(parent.context).inflate(R.layout.item_privacy_post, parent, false)) {
        override fun bind(obj: ICPrivacy) {
            itemView.btnIcon.setImageResource(if (obj.selected) {
                R.drawable.ic_radio_on_24dp
            } else {
                R.drawable.ic_radio_un_checked_gray_24dp
            })

            itemView.tvTitle.text = obj.privacyElementName
            itemView.tvDesc.text = obj.privacyElementDescription

            itemView.setOnClickListener {
                if (!obj.selected) {
                    for (item in listData) {
                        item.selected = false
                    }
                    obj.selected = true
                    notifyDataSetChanged()
                }
            }
        }
    }
}
package vn.icheck.android.component.product.noowner

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_no_owner.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.ichecklibs.ViewHelper

class NoOwnerHolder(parent: ViewGroup) : BaseViewHolder<NoOwnerModel>(LayoutInflater.from(parent.context).inflate(R.layout.item_no_owner, parent, false)) {

    override fun bind(obj: NoOwnerModel) {
        itemView.btn_send_owner.apply {
            background = ViewHelper.bgSecondaryCorners20(itemView.context)
            setOnClickListener {
                obj.listener.onContribute()
            }
        }
    }
}
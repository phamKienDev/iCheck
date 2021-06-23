package vn.icheck.android.component.product.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.item_list_distributor.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICPage

class VendorHolder(parent: ViewGroup) : BaseViewHolder<List<ICPage>>(LayoutInflater.from(parent.context).inflate(R.layout.item_list_distributor, parent, false)) {

    override fun bind(obj: List<ICPage>) {
        itemView.recyclerView.apply {
            layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = VendorAdapter(obj)
        }
    }
}
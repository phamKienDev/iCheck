package vn.icheck.android.component.product.vendor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list_distributor.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder

class VendorHolder(parent: ViewGroup, val recycledViewPool: RecyclerView.RecycledViewPool?) : BaseViewHolder<VendorModel>(LayoutInflater.from(parent.context).inflate(R.layout.item_list_distributor, parent, false)) {
    override fun bind(obj: VendorModel) {
        itemView.recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        itemView.recyclerView.setRecycledViewPool(recycledViewPool)
        itemView.recyclerView.adapter = VendorAdapter(obj.listVendor, obj.icon)
    }
}
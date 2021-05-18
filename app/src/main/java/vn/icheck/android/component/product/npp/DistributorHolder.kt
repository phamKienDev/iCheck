package vn.icheck.android.component.product.npp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.item_list_distributor.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.product.npp.adapter.DistributorAdapter
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.ui.StartSnapHelper
import vn.icheck.android.ui.layout.CustomGridLayoutManager

class DistributorHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_distributor, parent, false)) {

    init {
        val snap = LinearSnapHelper()
        snap.attachToRecyclerView(itemView.recyclerView)
    }

    fun bind(obj: DistributorModel, url: String) {
        itemView.recyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)

        val list = mutableListOf<ICPage>()
        for (i in 0 until obj.listBusiness.size) {
            if (i < 3) {
                list.add(obj.listBusiness[i])
            }
        }

        if (obj.listBusiness.size > 3) {
            list.add(ICPage())
        }

        itemView.recyclerView.adapter = DistributorAdapter(list, url)
    }
}
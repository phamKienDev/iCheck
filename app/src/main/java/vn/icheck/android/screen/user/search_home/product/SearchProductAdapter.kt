package vn.icheck.android.screen.user.search_home.product

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewSearchAdapter
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.component.collection.vertical.AdsProductVerticalHolderV2
import vn.icheck.android.network.models.ICProductTrend

class SearchProductAdapter(val callback: IRecyclerViewSearchCallback) : RecyclerViewSearchAdapter<ICProductTrend>(callback) {

    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return AdsProductVerticalHolderV2(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is AdsProductVerticalHolderV2) {
            holder.bind(listData[position])
        }
    }
}
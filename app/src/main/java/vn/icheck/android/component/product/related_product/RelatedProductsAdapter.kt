package vn.icheck.android.component.product.related_product

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.collection.vertical.AdsProductVerticalHolderV2
import vn.icheck.android.network.models.ICProductTrend

class RelatedProductsAdapter(val listData: MutableList<ICProductTrend>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsProductVerticalHolderV2 {
        return AdsProductVerticalHolderV2(parent)
    }

    override fun getItemCount(): Int {
        return if (listData.size > 6) {
            6
        } else {
            listData.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AdsProductVerticalHolderV2) {
            holder.bind(listData[position])
        }
    }
}
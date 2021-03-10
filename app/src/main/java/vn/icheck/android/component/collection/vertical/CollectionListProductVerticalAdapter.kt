package vn.icheck.android.component.collection.vertical

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.collection.vertical.AdsProductVerticalHolder
import vn.icheck.android.network.models.ICProduct

class CollectionListProductVerticalAdapter(private val listData: List<ICProduct>) : RecyclerView.Adapter<AdsProductVerticalHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsProductVerticalHolder {
        return AdsProductVerticalHolder(parent)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: AdsProductVerticalHolder, position: Int) {
        holder.bind(listData[position])
    }
}
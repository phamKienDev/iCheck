package vn.icheck.android.component.collection.horizontal

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.network.models.ICProduct

class CollectionListProductHorizontalAdapter(private val listData: List<ICProduct>) : RecyclerView.Adapter<CollectionProductHorizontalHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionProductHorizontalHolder {
        return CollectionProductHorizontalHolder(parent)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: CollectionProductHorizontalHolder, position: Int) {
        holder.bind(listData[position])
    }
}
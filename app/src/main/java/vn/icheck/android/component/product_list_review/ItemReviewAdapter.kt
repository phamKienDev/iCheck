package vn.icheck.android.component.product_list_review

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.component.`null`.NullHolder
import vn.icheck.android.component.product_review.list_review.ItemListReviewModel
import vn.icheck.android.screen.user.search_home.result.holder.ReviewSearchHolder

class ItemReviewAdapter(val type: Int? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ItemListReviewModel>()

    private val emptyType = 1
    private val itemType = 2

    fun setData(list: MutableList<ItemListReviewModel>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun updateReview(review: ItemListReviewModel) {
        for (i in 0 until listData.size) {
            if (listData[i].data.id == review.data.id) {
                listData[i] = review
                notifyItemChanged(i)
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNullOrEmpty())
            emptyType
        else
            itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ReviewSearchHolder(parent, type)
            else -> NullHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReviewSearchHolder) {
            holder.bind(listData[position].data)
        }
    }
}

package vn.icheck.android.screen.user.search_home.review

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.adapter.RecyclerViewSearchAdapter
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.screen.user.search_home.result.holder.ReviewSearchHolder

class SearchReviewAdapter(val callback: IRecyclerViewSearchCallback) : RecyclerViewSearchAdapter<ICPost>(callback) {
    override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ReviewSearchHolder(parent)
    }

    fun updateReview(post: ICPost) {
        val index = listData.indexOfFirst { it.id == post.id }
        if (index != -1) {
            getListData[index] = post
            notifyItemChanged(index)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is ReviewSearchHolder) {
            holder.bind(listData[position])
        }
    }
}
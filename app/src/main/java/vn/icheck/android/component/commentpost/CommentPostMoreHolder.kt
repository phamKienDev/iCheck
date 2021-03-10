package vn.icheck.android.component.commentpost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_comment_post_option.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener

class CommentPostMoreHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_post_option, parent, false)) {

    fun bind(obj: ICCommentPostMore, listener: ItemClickListener<ICCommentPostMore>) {
        if (obj.isLoading) {
            itemView.progressBar.visibility = View.VISIBLE
            itemView.btnMore.visibility = View.GONE
        } else {
            itemView.progressBar.visibility = View.GONE
            itemView.btnMore.apply {
                visibility = View.VISIBLE
                text = itemView.context.getString(R.string.xem_them_cau_tra_loi_xxx, obj.totalCount - obj.currentCount)

                setOnClickListener {
                    obj.isLoading = true
                    listener.onItemClick(adapterPosition, obj)
                }
            }
        }
    }
}
package vn.icheck.android.activities.product.review_product_v1.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_comment_review_product.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.HorizontalImageAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICProductReviews
import vn.icheck.android.activities.product.review_product_v1.view.IReviewProductView
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.CommentTimeUtil

class CommentReviewAdapter(val reviewPosition: Int, val listener: IReviewProductView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICProductReviews.Comments>()
    var totalCount = 0
    var commentId = -1L


    fun setData(list: MutableList<ICProductReviews.Comments>, totalCount: Int) {
        listData.clear()
        listData.addAll(list)
        this.totalCount = totalCount
        notifyDataSetChanged()
    }

    fun addComment(obj: ICProductReviews.Comments) {
        listData.add(0, obj)
        notifyItemInserted(0)
    }

    fun setCommentID(id: Long) {
        commentId = id
    }

    fun addList(list: MutableList<ICProductReviews.Comments>) {
        val currentPosition = listData.size
//        notifyItemChanged(currentPosition - 1)
        listData.addAll(list)
        notifyItemRangeInserted(currentPosition, list.size)
    }

    fun clearData() {
        listData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommentHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_review_product, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as CommentHolder
        listData[position].owner?.let {
            holder.bind(listData[position])
        }

    }

    inner class CommentHolder(view: View) : BaseViewHolder<ICProductReviews.Comments>(view) {
        fun hideShowMoreComment(positionShowMoreComment:Int) {
            if (adapterPosition == positionShowMoreComment) {
                    itemView.tv_xtc.visibility = View.GONE
                    itemView.progressBar.visibility = View.GONE
            }
        }

        override fun bind(obj: ICProductReviews.Comments) {
            //user
            val vgUserAvatar = itemView.findViewById<ViewGroup>(R.id.user_avatar)
            val avatar = vgUserAvatar.getChildAt(0) as CircleImageView
            if (obj.owner != null) {
                WidgetUtils.loadImageUrl(avatar, obj.owner!!.avatarThumb?.small, R.drawable.ic_avatar_default_84dp)
                itemView.tv_name.text = obj.owner!!.name
                itemView.tv_name.setOnClickListener {
                }
            }
            itemView.tv_time.text = CommentTimeUtil(obj).getTime()
            itemView.tv_comment.text = obj.activityValue

            if (obj.ownerType == "page") {
                avatar.borderWidth = 1
                avatar.borderColor = vn.icheck.android.ichecklibs.Constant.getSecondaryColor(itemView.context)
                itemView.tv_name.setTextColor(avatar.borderColor)
                vgUserAvatar.getChildAt(1).visibility = View.VISIBLE
            } else {
                avatar.borderWidth = 0
                itemView.tv_name.setTextColor(Color.BLACK)
                vgUserAvatar.getChildAt(1).visibility = View.GONE
            }

            vgUserAvatar.setOnClickListener {
                listener.onShowDetailUser(obj.owner?.id, obj.owner?.type)
            }
            itemView.tv_name.setOnClickListener {
                listener.onShowDetailUser(obj.owner?.id, obj.owner?.type)
            }

            //image
            val imageAdapter = HorizontalImageAdapter()
            itemView.rcv_image_comment.adapter = imageAdapter
            imageAdapter.clearData()
            itemView.rcv_image_comment.layoutManager = LinearLayoutManager(listener.mContext, LinearLayoutManager.HORIZONTAL, false)
            if (!obj.imageThumbs.isNullOrEmpty()) {
                val listImg = mutableListOf<String>()
                for (i in obj.imageThumbs) {
                    i.thumbnail?.let {
                        listImg.add(it)
                    }
                }
                imageAdapter.setData(listImg)
            }

            if (adapterPosition == itemCount - 1) {
                if (totalCount > listData.size) {
                    itemView.tv_xtc.apply {
                        text = context.getString(R.string.xem_them_x_binh_luan_khac, totalCount - listData.size)
                        visibility = View.VISIBLE
                    }
                } else {
                    itemView.tv_xtc.visibility = View.GONE
                    itemView.progressBar.visibility = View.GONE
                }
            } else {
                itemView.tv_xtc.visibility = View.GONE
                itemView.progressBar.visibility = View.GONE
            }

            itemView.tv_xtc.setOnClickListener {
                if (commentId != -1L) {
                    listener.onGetListComments(reviewPosition,adapterPosition, commentId, listData.size)
                    itemView.tv_xtc.visibility = View.INVISIBLE
                    itemView.progressBar.visibility = View.VISIBLE
                }
            }
        }

    }
}
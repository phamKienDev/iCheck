package vn.icheck.android.screen.user.bookmark_history

import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.databinding.ItemBookmarkBinding
import vn.icheck.android.network.model.bookmark.BookmarkHistoryResponse
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.model.bookmark.BookmarkHistoryResponse
import vn.icheck.android.util.ick.*

class BookmarkHistoryAdapter : PagingDataAdapter<BookmarkHistoryResponse, RecyclerView.ViewHolder>(DIFF_UTIL) {
    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<BookmarkHistoryResponse>() {
            override fun areItemsTheSame(oldItem: BookmarkHistoryResponse, newItem: BookmarkHistoryResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: BookmarkHistoryResponse, newItem: BookmarkHistoryResponse): Boolean {
                return oldItem.updatedAt == newItem.updatedAt
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        holder as BookmarkItemHolder
        if (!data?.name?.trim().isNullOrEmpty()) {
            holder.binding.tvNameProduct simpleText data?.name
            holder.binding.tvNameProduct.typeface =Typeface.createFromAsset(holder.binding.root.context.assets, "font/barlow_medium.ttf")
            holder.binding.tvNameProduct.setTextColor(Constant.getNormalTextColor(holder.binding.root.context))
        } else {
            holder.binding.tvNameProduct.typeface = Typeface.createFromAsset(holder.binding.root.context.assets, "font/barlow_semi_bold_italic.ttf")
            holder.binding.tvNameProduct simpleText "Tên đang cập nhật"
            holder.binding.tvNameProduct.setTextColor(Constant.getDisableTextColor(holder.binding.tvNameProduct.context))
        }
        holder.binding.imgProduct.loadRoundedImage(
                data?.media?.firstOrNull {
                    it?.type == "image"
                }?.content,
                R.drawable.default_product_image
        )
        holder.binding.tvBarcode simpleText data?.barcode
        if (data?.verified == true) {
            holder.binding.itemReviewRoot.tvVerify.beVisible()
        } else {
            holder.binding.itemReviewRoot.tvVerify.beGone()
        }
        if (data?.rating ?: 0.0 > 0f) {
            holder.binding.itemReviewRoot.ratingBar.rating = (data?.rating ?: 0.0).toFloat()
            holder.binding.itemReviewRoot.tvCountRating simpleText String.format("%.1f", (data?.rating
                    ?: 0.0) * 2)
            holder.binding.itemReviewRoot.ratingBar.beVisible()
            holder.binding.itemReviewRoot.tvCountRating.beVisible()
            holder.binding.tvNoReview.beGone()
        } else {
            holder.binding.itemReviewRoot.ratingBar.beGone()
            holder.binding.itemReviewRoot.tvCountRating.beGone()
            holder.binding.tvNoReview.beVisible()
        }
//        if (holder.binding.itemReviewRoot.tvVerify.visibility == View.GONE &&
//                holder.binding.itemReviewRoot.ratingBar.visibility == View.GONE) {
//            holder.binding.itemReviewRoot.root.beGone()
//            holder.binding.tvNoReview.beVisible()
//        } else {
//            holder.binding.itemReviewRoot.root.beVisible()
//            holder.binding.tvNoReview.beGone()
//        }
        if (data?.price ?: 0 > 0) {
            holder.binding.tvPrice simpleText (data?.price ?: 0L).getMoneyFormat()
            holder.binding.tvPrice.setTextColor(Constant.getSecondaryColor(holder.itemView.context))
            holder.binding.tvGiaNiemYet.beVisible()
            holder.binding.tvPrice.typeface = Typeface.createFromAsset(holder.binding.root.context.assets, "font/barlow_medium.ttf")
            holder.binding.tvPrice.textSize = 20f
        } else {
            holder.binding.tvPrice.typeface = Typeface.createFromAsset(holder.binding.root.context.assets, "font/barlow_semi_bold_italic.ttf")
            holder.binding.tvPrice simpleText "Giá đang cập nhật"
            holder.binding.tvPrice.setTextColor(Constant.getDisableTextColor(holder.itemView.context))
            holder.binding.tvGiaNiemYet.beGone()
            holder.binding.tvPrice.textSize = 14f
        }
        holder.binding.btnLike.setCustomChecked(true) { _, checked ->
            EventBus.getDefault().post(hashMapOf("id" to data?.id, "checked" to checked))
        }
        holder.binding.imgProduct.setOnClickListener {
            EventBus.getDefault().post(hashMapOf("id" to data?.id))
        }
        holder.binding.tvNameProduct.setOnClickListener {
            EventBus.getDefault().post(hashMapOf("id" to data?.id))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lf = parent.getLayoutInflater()
        return BookmarkItemHolder(ItemBookmarkBinding.inflate(lf, parent, false))
    }

    inner class BookmarkItemHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root)
}
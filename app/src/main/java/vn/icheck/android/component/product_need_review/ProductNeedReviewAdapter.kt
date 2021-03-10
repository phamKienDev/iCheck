package vn.icheck.android.component.product_need_review

import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_need_review.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.TextHelper.setTextNameProduct
import vn.icheck.android.network.models.product_need_review.ICProductNeedReview
import vn.icheck.android.screen.user.campaign.calback.IProductNeedReviewListener
import vn.icheck.android.screen.user.list_product_review.ListProductReviewActivity
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductNeedReviewAdapter(private val listData: MutableList<ICProductNeedReview>, val listener: IProductNeedReviewListener) : RecyclerView.Adapter<ProductNeedReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_product_need_review, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)

        holder.itemView.layoutProduct.setOnClickListener {
            // chuyển sang màn chi tiết sản phầm
            listener.onItemClickProduct(position, item)
        }

        holder.itemView.layoutRatingBar.setOnClickListener {
            /* Bật BottomSheet rating
            * ( Truyền position và id sang nếu cần để nếu khi rating thành công sẽ xóa item đó đi )
            */
            listener.onItemClickReview(position, item)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICProductNeedReview) {
            if (!item.media.isNullOrEmpty()) {
                if (item.media!![0].content != null) {
                    WidgetUtils.loadImageUrlRounded4(itemView.imgAvatar, item.media?.get(0)?.content, R.drawable.product_images_new_placeholder)
                } else {
                    WidgetUtils.loadImageUrlRounded4(itemView.imgAvatar, null, R.drawable.product_images_new_placeholder)
                }
            } else {
                WidgetUtils.loadImageUrlRounded4(itemView.imgAvatar, null, R.drawable.product_images_new_placeholder)
            }

            itemView.tvName.setTextNameProduct(item.name)
            itemView.tvNameBusiness.text = if (!item.owner?.name.isNullOrEmpty()) item.owner?.name else itemView.context.getString(R.string.dang_cap_nhat)

            itemView.ratingBar.isClickable = false
        }
    }

}
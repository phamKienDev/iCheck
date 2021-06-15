package vn.icheck.android.component.product_review.header_list_review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_header_info_review_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.text.ReviewPointText

class HeaderListReviewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_header_info_review_holder, parent, false)) {
    fun bind(header: HeaderListReviewModel) {
        val obj = header.data
        itemView.tvPoint.rText(R.string.format_1_f, obj.averagePoint * 2)
        itemView.tvCountReview.rText(R.string.d_danh_gia, obj.ratingCount.toInt())
        itemView.tvRating.text = ReviewPointText.getText(obj.averagePoint)
    }
}
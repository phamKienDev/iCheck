package vn.icheck.android.component.product.reviewsummary

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_product_total_reviews.view.*
import kotlinx.android.synthetic.main.item_product_total_reviews.view.layoutEmptyReview
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.screen.user.product_detail.product.model.IckReviewSummaryModel
import vn.icheck.android.screen.user.list_product_review.ListProductReviewActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.text.ReviewPointText

class ReviewSummaryHolder(parent: ViewGroup) : BaseViewHolder<IckReviewSummaryModel>(LayoutInflater.from(parent.context).inflate(R.layout.item_product_total_reviews, parent, false)) {

    @SuppressLint("SetTextI18n")
    override fun bind(obj: IckReviewSummaryModel) {
        if (obj.obj.ratingCount <= 0f) {
            itemView.containerSummary.beGone()
            itemView.layoutEmptyReview.beVisible()

            itemView.layoutEmptyReview.setOnClickListener {
                IckProductDetailActivity.INSTANCE?.scrollWithViewType(ICViewTypes.SUBMIT_REVIEW_TYPE)
            }
        } else {
            itemView.containerSummary.beVisible()
            itemView.layoutEmptyReview.beGone()

            itemView.ratingBar.rating = obj.obj.averagePoint

            ReviewPointText.getTextTotalProductDetail(obj.obj.averagePoint,itemView.tvRatingCount)

            if (obj.obj.willShare.toInt() == 0){
                itemView.tvSuggest.beInvisible()
            } else {
                itemView.tvSuggest.rText(R.string.d_se_gioi_thieu_cho_ban_be, obj.obj.willShare.toInt())
            }

            itemView.tvViewAll.rText(R.string.xem_s_danh_gia, TextHelper.formatMoney(obj.obj.ratingCount))

            itemView.tvViewAll.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    ListProductReviewActivity.startActivity(obj.id, activity)
                }
            }
        }
    }
}
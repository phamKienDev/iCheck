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
                itemView.tvSuggest.text = "${obj.obj.willShare.toInt()}% Sẽ giới thiệu cho bạn bè"
            }

            itemView.tvViewAll.text = ("Xem " + TextHelper.formatMoney(obj.obj.ratingCount) + " đánh giá")

            itemView.tvViewAll.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    ListProductReviewActivity.startActivity(obj.id, activity)
                }
            }
        }
    }
}
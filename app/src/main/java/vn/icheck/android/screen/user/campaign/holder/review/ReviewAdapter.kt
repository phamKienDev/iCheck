package vn.icheck.android.screen.user.campaign.holder.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_home_review.view.*
import vn.icheck.android.R
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICReview
import vn.icheck.android.screen.user.campaign.calback.IReviewListener
import vn.icheck.android.util.kotlin.WidgetUtils

class ReviewAdapter(val listData: MutableList<ICReview>, private val reviewListener: IReviewListener?) : PagerAdapter() {

    override fun isViewFromObject(view: View, obj: Any): Boolean = obj == view

    override fun getCount(): Int = listData.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context).inflate(R.layout.item_home_review, container, false)

        val review = listData[position]

        if (review.product != null) {
            itemView.layoutProduct.visibility = View.VISIBLE

            WidgetUtils.loadImageUrlRounded6(itemView.imgProduct, review.product?.thumbnails?.small)
            itemView.txtProductName.text = review.product?.name
            itemView.tvProductPrice.text = (TextHelper.formatMoney(review.product?.price) + " đ")
        } else {
            itemView.layoutProduct.visibility = View.GONE
        }

        review.account?.let {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, it.avatar_thumbnails?.small)
            itemView.tvName.text = it.name
        }

        itemView.txtDate.text = TimeHelper.convertDateTimeSvToDateTimeVn(review.updated_at)
        itemView.ratingBar.rating = review.rating.toFloat()
        itemView.txtContent.text = review.content

        val attachments = review.attachments

        if (attachments != null && attachments.isNotEmpty()) {
            itemView.layoutImage.visibility = View.VISIBLE

            WidgetUtils.loadImageUrlRounded4(itemView.img1, attachments[0].thumbnails?.small)

            if (attachments.size > 1) {
                WidgetUtils.loadImageUrlRounded4(itemView.img2, attachments[1].thumbnails?.small)

                if (attachments.size > 2) {
                    WidgetUtils.loadImageUrlRounded4(itemView.img2, attachments[1].thumbnails?.small)

                    if (attachments.size > 3) {
                        itemView.txtProductCount.visibility = View.VISIBLE
                        itemView.txtProductCount.text = ("+${(attachments.size - 3)}")
                    } else {
                        itemView.txtProductCount.visibility = View.GONE
                    }
                } else {
                    itemView.img3.visibility = View.INVISIBLE
                    itemView.txtProductCount.visibility = View.GONE
                }
            } else {
                itemView.img2.visibility = View.INVISIBLE
                itemView.img3.visibility = View.INVISIBLE
            }

            itemView.img1.setOnClickListener {
                reviewListener?.onReviewImageClicked(0, attachments)
            }

            itemView.img2.setOnClickListener {
                reviewListener?.onReviewImageClicked(1, attachments)
            }

            itemView.img3.setOnClickListener {
                reviewListener?.onReviewImageClicked(2, attachments)
            }

            itemView.txtProductCount.setOnClickListener {
                reviewListener?.onReviewImageClicked(3, attachments)
            }
        } else {
            itemView.layoutImage.visibility = View.GONE
        }

        itemView.setOnClickListener {
            reviewListener?.onReviewClicked(review)
        }

        itemView.layoutAccount.setOnClickListener {
            review.account?.let { account ->
                reviewListener?.onReviewAccountClicked(account)
            }
        }

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }
}
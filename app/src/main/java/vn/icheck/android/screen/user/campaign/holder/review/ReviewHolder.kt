package vn.icheck.android.screen.user.campaign.holder.review

import android.view.View
import android.widget.LinearLayout
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICReview
import vn.icheck.android.screen.user.campaign.calback.IReviewListener
import vn.icheck.android.ui.view.HeightWrappingViewPager

class ReviewHolder(view: View, private val reviewListener: IReviewListener?) : BaseViewHolder<MutableList<ICReview>>(view) {

    override fun bind(obj: MutableList<ICReview>) {
        val viewPager = (itemView as LinearLayout).getChildAt(0) as HeightWrappingViewPager

        viewPager.removeAllViews()
        viewPager.adapter = ReviewAdapter(obj, reviewListener)
    }
}
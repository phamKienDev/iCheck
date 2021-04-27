package vn.icheck.android.component.product_need_review

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.network.models.product_need_review.ICProductNeedReview
import vn.icheck.android.screen.user.campaign.calback.IProductNeedReviewListener

class ProductNeedReviewHolder(parent: ViewGroup,val listener: IProductNeedReviewListener) : BaseViewHolder<MutableList<ICProductNeedReview>>(ViewHelper.createProductNeedReview(parent.context)) {

    override fun bind(obj: MutableList<ICProductNeedReview>) {
        (itemView as ViewGroup).run {
            (getChildAt(1) as RecyclerView).run {
                layoutManager = LinearLayoutManager(itemView.context.applicationContext, LinearLayoutManager.HORIZONTAL, false)
                adapter = ProductNeedReviewAdapter(obj,listener)
            }
        }
    }
}
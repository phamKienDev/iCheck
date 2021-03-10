package vn.icheck.android.screen.user.campaign.calback

import vn.icheck.android.network.models.product_need_review.ICProductNeedReview

interface IProductNeedReviewListener {
    fun onItemClickReview(position:Int,item: ICProductNeedReview)
    fun onItemClickProduct(position:Int,item: ICProductNeedReview)
}
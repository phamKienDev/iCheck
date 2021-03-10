package vn.icheck.android.component.product_review.submit_review

import vn.icheck.android.component.product_review.my_review.IMyReviewListener
import vn.icheck.android.network.models.ICPost

interface ISubmitReviewListener : IMyReviewListener {
    fun onTakeImage(positionHolder: Int)
    fun onPostReviewSuccess(obj: ICPost)
}
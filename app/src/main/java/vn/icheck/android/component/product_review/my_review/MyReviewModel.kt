package vn.icheck.android.component.product_review.my_review

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICProductMyReview

data class MyReviewModel (val data: ICProductMyReview):ICViewModel{
    override fun getTag(): String {
        return ICViewTags.MY_REVIEW_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.MY_REVIEW_TYPE
    }
}
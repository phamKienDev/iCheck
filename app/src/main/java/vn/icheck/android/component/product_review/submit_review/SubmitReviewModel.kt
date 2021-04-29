package vn.icheck.android.component.product_review.submit_review

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICCriteriaReview

data class SubmitReviewModel(var data: MutableList<ICCriteriaReview>, val productId: Long) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.SUBMIT_REVIEW_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.SUBMIT_REVIEW_TYPE
    }
}
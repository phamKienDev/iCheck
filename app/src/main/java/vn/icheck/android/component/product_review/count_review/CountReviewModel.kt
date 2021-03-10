package vn.icheck.android.component.product_review.count_review

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes

data class CountReviewModel(val count: Int, val productId: Long = -1L) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.COUNT_REVIEW_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.COUNT_REVIEW_TYPE
    }

}
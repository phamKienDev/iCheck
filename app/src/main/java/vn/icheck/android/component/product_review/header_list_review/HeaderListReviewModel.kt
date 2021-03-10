package vn.icheck.android.component.product_review.header_list_review

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICReviewSummary

data class HeaderListReviewModel(val data: ICReviewSummary) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.HEADER_REVIEW_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.HEADER_REVIEW_TYPE
    }

}
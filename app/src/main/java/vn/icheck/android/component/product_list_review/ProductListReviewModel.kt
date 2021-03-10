package vn.icheck.android.component.product_list_review

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICPost

class ProductListReviewModel(val data: MutableList<ICPost>, val count: Int, val productId: Long) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.LIST_REVIEW_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.LIST_REVIEWS_TYPE
    }
}
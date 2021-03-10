package vn.icheck.android.component.product_review.list_review

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICPost

class ItemListReviewModel(var data: ICPost) : ICViewModel {


    override fun getTag(): String {
        return ICViewTags.ITEM_REVIEW_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.ITEM_REVIEWS_TYPE
    }
}
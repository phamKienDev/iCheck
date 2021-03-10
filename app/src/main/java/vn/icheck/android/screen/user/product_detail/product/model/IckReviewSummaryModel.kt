package vn.icheck.android.screen.user.product_detail.product.model

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICReviewSummary

data class IckReviewSummaryModel(val id: Long, val obj: ICReviewSummary): ICViewModel {

    override fun getTag(): String = ""

    override fun getViewType(): Int = ICViewTypes.REVIEW_SUMMARY_TYPE
}
package vn.icheck.android.component.product_question_answer

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.base.ICListResponse

class ProductQuestionModel(val data: ICListResponse<Any>, val productId: Long, var barcode: String?) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.QUESTIONS_ANSWER_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.QUESTIONS_ANSWER_TYPE
    }
}
package vn.icheck.android.component.product.emty_qa

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes

class EmptyQAModel(val idProduct: Long) : ICViewModel {

    override fun getTag(): String {
        return ""
    }

    override fun getViewType(): Int {
        return ICViewTypes.EMPTY_QA_TYPE
    }
}
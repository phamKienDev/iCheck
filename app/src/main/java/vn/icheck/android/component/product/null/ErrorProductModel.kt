package vn.icheck.android.component.product.`null`

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes

class ErrorProductModel(val error: Int, val message: Int) : ICViewModel {

    override fun getTag(): String = ""

    override fun getViewType(): Int {
        return ICViewTypes.ERROR
    }
}
package vn.icheck.android.component.product.infor

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes

class DescriptionModel (val title: String, val content: String) : ICViewModel {

    override fun getTag(): String {
        return ICViewTags.INFO
    }

    override fun getViewType(): Int {
        return ICViewTypes.DESCRIPTION_TYPE
    }
}
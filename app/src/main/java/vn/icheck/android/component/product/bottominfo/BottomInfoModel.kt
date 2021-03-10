package vn.icheck.android.component.product.bottominfo

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes

class BottomInfoModel : ICViewModel {

    override fun getTag(): String {
        return ICViewTags.CONTACT_ICHECK
    }

    override fun getViewType(): Int {
        return ICViewTypes.TYPE_BOTTOM
    }
}
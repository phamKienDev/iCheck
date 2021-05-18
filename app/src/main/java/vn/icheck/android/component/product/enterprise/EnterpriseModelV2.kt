package vn.icheck.android.component.product.enterprise

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICOwner

class EnterpriseModelV2(val business: ICOwner?, val icon: Int?, val background: Int) : ICViewModel {

    override fun getTag(): String {
        return ICViewTags.ENTERPRISE_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.ENTERPRISE_TYPE
    }
}
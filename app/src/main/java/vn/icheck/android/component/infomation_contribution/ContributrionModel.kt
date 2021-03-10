package vn.icheck.android.component.infomation_contribution

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICOwner
import vn.icheck.android.network.models.ICProductContribution
import vn.icheck.android.network.models.product_detail.ICManager

data class ContributrionModel(var data: ICProductContribution?, val productId: Long, val productVerify: Boolean, val barcode: String, val owner: ICOwner? = null, val manager: ICManager? = null) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.CONTRIBUTE_MODULE
    }

    override fun getViewType(): Int {
        return ICViewTypes.CONTRIBUTE_USER
    }
}
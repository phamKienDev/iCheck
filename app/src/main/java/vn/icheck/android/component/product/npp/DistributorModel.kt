package vn.icheck.android.component.product.npp

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICPage

class DistributorModel(val listBusiness: MutableList<ICPage>, val productId:Long) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.DISTRIBUTOR
    }

    override fun getViewType(): Int {
        return ICViewTypes.DISTRIBUTOR
    }
}
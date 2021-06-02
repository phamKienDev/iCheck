package vn.icheck.android.component.product.vendor

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICPage

class VendorModel(val listVendor: MutableList<ICPage>) : ICViewModel {

    override fun getTag(): String {
        return ICViewTags.VENDOR
    }

    override fun getViewType(): Int {
        return ICViewTypes.VENDOR_TYPE
    }
}
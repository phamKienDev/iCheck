package vn.icheck.android.component.product.verified

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICBarcodeProductV2

class ProductVerifiedModel(val icBarcodeProductV2: ICBarcodeProductV2) : ICViewModel {

    override fun getTag(): String {
        return ICViewTags.VERIFIED_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.VERIFIED_TYPE
    }
}
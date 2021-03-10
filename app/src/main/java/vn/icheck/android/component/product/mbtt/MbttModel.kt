package vn.icheck.android.component.product.mbtt

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICTransparency

class MbttModel(var icTransparency: ICTransparency,val idProduct : Long) : ICViewModel {

    override fun getTag(): String {
        return ICViewTags.MMB
    }

    override fun getViewType(): Int {
        return ICViewTypes.TRANSPARENCY_TYPE
    }
}
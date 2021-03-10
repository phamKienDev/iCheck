package vn.icheck.android.component.product.certifications

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes

class CertificationsModel(val certificates: List<String>) : ICViewModel {

    override fun getTag(): String {
        return ICViewTags.CHUNG_CHI_MODULE
    }

    override fun getViewType(): Int {
        return ICViewTypes.CHUNG_CHI_TYPE
    }
}
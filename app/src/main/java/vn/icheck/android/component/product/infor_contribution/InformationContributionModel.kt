package vn.icheck.android.component.product.infor_contribution

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes

class InformationContributionModel (val barcode:String) : ICViewModel {
    override fun getTag(): String = ""

    override fun getViewType(): Int {
        return ICViewTypes.EMPTY_CONTRIBUTION_INTERPRISE_TYPE
    }
}
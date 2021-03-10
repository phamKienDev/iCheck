package vn.icheck.android.screen.user.product_detail.product.model

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICProductInformations

data class IckProductInformationModel(val productID: Long, val obj: ICProductInformations): ICViewModel {

    override fun getTag(): String = ""

    override fun getViewType(): Int = ICViewTypes.DESCRIPTION_TYPE
}
package vn.icheck.android.component.product.notverified

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICClientSetting
import vn.icheck.android.network.models.product_detail.ICDataProductDetail

data class ProductNotVerifiedModel(val page: ICDataProductDetail?, val data: ICClientSetting) : ICViewModel {

    override fun getTag(): String = "verification"

    override fun getViewType(): Int = ICViewTypes.NOT_VERIFIED_TYPE
}
package vn.icheck.android.component.product.header

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.product_detail.ICBasicInforProduct
import vn.icheck.android.network.models.product_detail.ICDataProductDetail

class ProductHeaderModel(
        var icBarcodeProduct: ICBasicInforProduct,
        var dataProductDetail: ICDataProductDetail?
): ICViewModel {

    override fun getTag(): String {
        return ICViewTags.HEADER_COMPONENT
    }

    override fun getViewType(): Int {
        return ICViewTypes.HEADER_TYPE
    }
}
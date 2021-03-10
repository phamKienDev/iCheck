package vn.icheck.android.component.shopvariant.product_detail

import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.ICViewTags
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.models.ICShopVariant
import vn.icheck.android.network.models.ICShopVariantV2

class ShopProductModel(val listShop: MutableList<ICShopVariantV2>) : ICViewModel {
    override fun getTag(): String {
        return ICViewTags.SHOP
    }

    override fun getViewType(): Int {
        return ICViewTypes.SHOP_VARIANT_TYPE
    }
}
package vn.icheck.android.screen.user.list_shop_variant.view

import vn.icheck.android.network.models.ICShopVariantV2

interface IListShopVariantView {
    fun onClickAddToCart(id: Long)
    fun onClickShowMap(location: ICShopVariantV2)
}
package vn.icheck.android.screen.user.listproductecommerce.viewmodel

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.feature.product.ProductInteractor

class ListProductsECommerceViewModel: BaseViewModel() {
    private val repository = ProductInteractor()

    var barcode: String = ""

    fun getProductsECommerce() = request { repository.getProductsECommerce(barcode) }
}
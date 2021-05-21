package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.viewmodel

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import javax.inject.Inject

class UpdateInformationFirstViewModel @Inject constructor() : BaseViewModel() {
    private val repository = DetailStampRepository()

    var productID: Long = 0

    suspend fun getVariantProduct(productId: Long) = repository.getProductVariant(productID = productId)


}
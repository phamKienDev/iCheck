package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.viewmodel

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import vn.icheck.android.network.models.detail_stamp_v6_1.ICFieldGuarantee
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1
import javax.inject.Inject

class UpdateInformationFirstViewModel @Inject constructor() : BaseViewModel() {
    private val repository = DetailStampRepository()

    var productID = 0L
    var updateType = 0
    var distributorID = 0L
    var phoneNumber: String? = null
    var productCode: String? = null
    var serial = ""
    var objVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant? = null
    var barcode = ""

    suspend fun getProductVariant() =  try {
        repository.getProductVariant(productID = productID).data
    } catch (e: Exception) {
        null
    }

    suspend fun getCustomerVariant() = try {
        repository.getCustomerVariant(barcode = barcode).data
    } catch (e: Exception) {
        null
    }

    suspend fun getGuaranteeVariant() = try {
        repository.getGuaranteeVariant(barcode = barcode).data
    } catch (e: Exception) {
        null
    }

    fun getGuaranteeCustomerDetail(phoneNumber: String) = request { repository.getGuaranteeCustomerDetail(distributorID, phoneNumber) }

}
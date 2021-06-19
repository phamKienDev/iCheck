package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.viewmodel

import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.feature.detail_stamp_v6_1.DetailStampRepository
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1
import javax.inject.Inject

class UpdateInformationFirstViewModel @Inject constructor() : BaseViewModel() {
    private val repository = DetailStampRepository()

    var productID: Long = 0L
        get() = field
        set(value) {
            field = value
        }
    var updateType: Int = 0
        get() = field
        set(value) {
            field = value
        }
    var distributorID: Long = 0L
        get() = field
        set(value) {
            field = value
        }
    var phoneNumber: String? = null
        get() = field
        set(value) {
            field = value
        }
    var productCode: String? = null
        get() = field
        set(value) {
            field = value
        }
    var serial: String = ""
        get() = field
        set(value) {
            field = value
        }
    var objVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant? = null
        get() = field
        set(value) {
            field = value
        }
    var barcode: String = ""
        get() = field
        set(value) {
            field = value
        }

    suspend fun getProductVariant() = try {
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
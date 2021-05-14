package vn.icheck.android.loyalty.screen.gift_detail_voucher

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.model.ICKScanVoucher
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.CampaignRepository

class GiftVoucherStaffViewModel : BaseViewModel<Any>() {
    private val repository = CampaignRepository()

    val scanSuccess = MutableLiveData<ICKScanVoucher?>()

    fun scanVoucher(voucher: String) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        repository.dispose()

        repository.scanVoucher(voucher, object : ICApiListener<ICKResponse<ICKScanVoucher>> {
            override fun onSuccess(obj: ICKResponse<ICKScanVoucher>) {
                scanSuccess.postValue(obj.data)
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true)
            }
        })
    }
}
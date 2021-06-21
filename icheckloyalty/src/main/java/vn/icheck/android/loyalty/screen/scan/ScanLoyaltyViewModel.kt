package vn.icheck.android.loyalty.screen.scan

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ichecklibs.util.RStringUtils.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.PointHelper
import vn.icheck.android.loyalty.model.ICKAccumulatePoint
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository

class ScanLoyaltyViewModel : BaseViewModel<Any>() {
    private val repository = RedeemPointRepository()

    val onAccumulatePoint = MutableLiveData<ICKAccumulatePoint>()
    val onInvalidTarget = MutableLiveData<String>()
    val onUsedTarget = MutableLiveData<String>()
    val onCustomer = MutableLiveData<String>()
    val onErrorString = MutableLiveData<String>()

    fun postAccumulatePoint(target: String) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        repository.postAccumulatePoint(collectionID, null, target, object : ICApiListener<ICKResponse<ICKAccumulatePoint>> {
            override fun onSuccess(obj: ICKResponse<ICKAccumulatePoint>) {
                if (obj.statusCode != 200) {
                    when(obj.status){
                        "INVALID_TARGET" -> {
                            onInvalidTarget.postValue(rText(R.string.ma_qrcode_cua_san_pham_nay_n_khong_thuoc_chuong_trinh))
                        }
                        "USED_TARGET" -> {
                            onUsedTarget.postValue(rText(R.string.ma_qrcode_cua_san_pham_nay_n_khong_con_diem_cong))
                        }
                        "INVALID_CUSTOMER" -> {
                            onCustomer.postValue(rText(R.string.ban_khong_thuoc_danh_sach_tham_gia_chuong_trinh))
                        }
                        else -> {
                            onErrorString.postValue(obj.data?.message)
                        }
                    }
                } else {
                    PointHelper.updatePoint(collectionID)
                    obj.data?.let {
                        onAccumulatePoint.postValue(it)
                    }
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}
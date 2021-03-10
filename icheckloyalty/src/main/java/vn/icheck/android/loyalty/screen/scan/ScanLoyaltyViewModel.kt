package vn.icheck.android.loyalty.screen.scan

import androidx.lifecycle.MutableLiveData
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
                            onInvalidTarget.postValue("Mã QRcode của sản phẩm này\nkhông thuộc chương trình")
                        }
                        "USED_TARGET" -> {
                            onUsedTarget.postValue("Mã QRcode của sản phẩm này\nkhông còn điểm cộng")
                        }
                        "INVALID_CUSTOMER" -> {
                            onCustomer.postValue("Bạn không thuộc danh sách\ntham gia chương trình")
                        }
                        else -> {
                            onErrorString.postValue(obj.data?.message)
                        }
                    }
                } else {
                    PointHelper.updatePoint(collectionID)
                    onAccumulatePoint.postValue(obj.data)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}
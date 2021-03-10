package vn.icheck.android.screen.user.barcode_qr_of_me

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.ICMyID

class QrAndBarcodeOfMeViewModel : ViewModel() {
    val userInteractor = UserInteractor()

    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<String>()

    fun getMyID(){
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())){
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        userInteractor.getMyID(object : ICNewApiListener<ICResponse<ICMyID>> {
            override fun onSuccess(obj: ICResponse<ICMyID>) {
                if (obj.data != null){
                    if (obj.data!!.myId.isNotEmpty()){
                        liveData.postValue(obj.data!!.myId)
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }
}
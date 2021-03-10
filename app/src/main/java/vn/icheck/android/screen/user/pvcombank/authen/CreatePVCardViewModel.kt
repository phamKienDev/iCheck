package vn.icheck.android.screen.user.pvcombank.authen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.pvcombank.PVcomBankRepository
import vn.icheck.android.network.models.pvcombank.ICAuthenPVCard
import vn.icheck.android.network.models.ICClientSetting

class CreatePVCardViewModel : ViewModel() {
    private val interactor = PVcomBankRepository()
    val onLinkAuth = MutableLiveData<ICAuthenPVCard>()

    val onLinkCreate = MutableLiveData<ICClientSetting>()
    val onError = MutableLiveData<ICError>()
    val onState = MutableLiveData<ICMessageEvent>()

    fun getSettingPVCombank(key: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        SettingHelper.getSystemSetting(key, "pvcombank", object : ISettingListener {
            override fun onRequestError(error: String) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }

            override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                if (!list.isNullOrEmpty())
                    onLinkCreate.postValue(list[0])
            }
        })
    }

    fun getLinkFormAuth() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))

        interactor.getFormLogin(object : ICNewApiListener<ICResponse<ICAuthenPVCard>> {
            override fun onSuccess(obj: ICResponse<ICAuthenPVCard>) {
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                if (!obj.data?.sessionId.isNullOrEmpty()) {
                    onLinkAuth.postValue(obj.data)
                }else{
                    onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}
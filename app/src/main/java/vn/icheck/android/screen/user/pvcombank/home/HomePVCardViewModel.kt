package vn.icheck.android.screen.user.pvcombank.home

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.callback.ISettingListener
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SettingHelper
import vn.icheck.android.network.feature.pvcombank.PVcomBankRepository
import vn.icheck.android.network.models.ICClientSetting

class HomePVCardViewModel : BaseViewModel() {
    private val repository = PVcomBankRepository()

    val onError = MutableLiveData<ICError>()
    val onUrl = MutableLiveData<ICClientSetting>()
    val onState = MutableLiveData<ICMessageEvent>()

    fun getDetailCardV2() = request { repository.getMyListCards() }

    fun getUrlUsagePolicy() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))
        SettingHelper.getSystemSetting("pvcombank.user-privacy", "pvcombank", object : ISettingListener {
            override fun onRequestError(error: String) {
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }

            override fun onGetClientSuccess(list: MutableList<ICClientSetting>?) {
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                if (!list.isNullOrEmpty())
                    onUrl.postValue(list[0])
            }
        })
    }

    fun getKyc() = request { repository.getKyc() }
}
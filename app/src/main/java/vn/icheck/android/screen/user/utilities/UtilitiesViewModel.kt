package vn.icheck.android.screen.user.utilities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.utility.UtilityRepository
import vn.icheck.android.network.models.ICTheme

class UtilitiesViewModel : ViewModel() {
    private val interactor = UtilityRepository()

    val onSetData = MutableLiveData<MutableList<ICTheme>>()
    val onAddData = MutableLiveData<MutableList<ICTheme>>()
    val onError = MutableLiveData<ICError>()
    val onState = MutableLiveData<ICMessageEvent>()

    var offset = 0

    fun getAllUtility(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadMore) {
            offset = 0
            onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_SHOW_LOADING))
        }

        interactor.getAllUtilities(object : ICNewApiListener<ICResponse<MutableList<ICTheme>>> {
            override fun onSuccess(obj: ICResponse<MutableList<ICTheme>>) {
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))

                for (item in obj.data ?: mutableListOf()) {
                    if (!item.secondary_functions.isNullOrEmpty()) {
                        for (i in item.secondary_functions!!.size - 1 downTo 0) {
                            if (item.secondary_functions!![i].scheme == "icheck://utilities") {
                                item.secondary_functions!!.removeAt(i)
                            }
                        }
                    }
                }

                if (isLoadMore)
                    onAddData.postValue(obj.data)
                else
                    onSetData.postValue(obj.data)
            }

            override fun onError(error: ICResponseCode?) {
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))
                onError.postValue(ICError(R.drawable.ic_error_request, error?.message
                        ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }

        })

    }
}
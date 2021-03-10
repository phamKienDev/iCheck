package vn.icheck.android.screen.user.listnotification.productnotice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.notification.NotificationInteractor
import vn.icheck.android.network.models.ICLayoutData
import vn.icheck.android.network.models.ICNotification

class ListProductNoticeViewModel() : ViewModel() {
    private val interaction = NotificationInteractor()

    val onSetData = MutableLiveData<MutableList<ICNotification>>()
    val onAddData = MutableLiveData<MutableList<ICNotification>>()
    val onError = MutableLiveData<ICError>()

    var offset = 0

    fun getFriendRequest(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            interaction.dispose()
            offset = 0
        }

        interaction.getListNotification(null, 1, offset, APIConstants.LIMIT, object : ICNewApiListener<ICLayoutData<ICListResponse<ICNotification>>> {
            override fun onSuccess(obj: ICLayoutData<ICListResponse<ICNotification>>) {
                offset += APIConstants.LIMIT

                if (isLoadMore) {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                } else {
                    onSetData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)))
            }
        })
    }
}
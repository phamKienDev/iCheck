package vn.icheck.android.screen.user.suggest_topic

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.suggest.SuggestInteractor
import vn.icheck.android.network.models.ICSuggestTopic

class SuggestTopicViewModel : ViewModel() {
    val onSetTopicData = MutableLiveData<MutableList<ICSuggestTopic>>()
    val onAddTopicData = MutableLiveData<MutableList<ICSuggestTopic>>()
    val onError = MutableLiveData<ICError>()

    val topicInteractor = SuggestInteractor()
    var offset = 0

    fun getListTopic(isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }
        if (!isLoadmore) {
            offset = 0
        }
        topicInteractor.getListTopic(offset, object : ICNewApiListener<ICResponse<ICListResponse<ICSuggestTopic>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICSuggestTopic>>) {
                offset += APIConstants.LIMIT
                if (isLoadmore) {
                    onAddTopicData.postValue(obj.data!!.rows)
                } else {
                    onSetTopicData.postValue(obj.data!!.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }

}
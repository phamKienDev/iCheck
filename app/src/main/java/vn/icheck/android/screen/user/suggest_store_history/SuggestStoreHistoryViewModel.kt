package vn.icheck.android.screen.user.suggest_store_history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.history.HistoryInteractor
import vn.icheck.android.network.models.history.ICSuggestStoreHistory
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone

class SuggestStoreHistoryViewModel : ViewModel() {

    private val interactor = HistoryInteractor()

    private var offset = 0

    val listData = MutableLiveData<MutableList<ICSuggestStoreHistory>>()
    val isLoadMoreData = MutableLiveData<Boolean>()
    val errorData = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    fun getSuggestStore(isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            if (!isLoadMore) {
                offset = 0
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.getSuggestStoreHistory(offset,object :ICNewApiListener<ICResponse<ICListResponse<ICSuggestStoreHistory>>>{
                override fun onSuccess(obj: ICResponse<ICListResponse<ICSuggestStoreHistory>>) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    if (obj.data?.rows != null && !obj.data?.rows.isNullOrEmpty()) {
                        offset += APIConstants.LIMIT
                        if (!isLoadMore) {
                            isLoadMoreData.postValue(true)
                        }
                        listData.postValue(obj.data?.rows)
                    } else {
                        if (!isLoadMore) {
                            errorData.postValue(Constant.ERROR_EMPTY)
                        }
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    errorData.postValue(Constant.ERROR_SERVER)
                }
            })
        }
    }
}
package vn.icheck.android.screen.user.history_search

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
import vn.icheck.android.network.models.history.ICItemHistory

class HistorySearchViewModel : ViewModel() {

    private val interactor = HistoryInteractor()

    var offset = 0

    val cartCount = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val listData = MutableLiveData<MutableList<ICItemHistory>>()
    val isLoadMoreData = MutableLiveData<Boolean>()
    val errorData = MutableLiveData<Int>()

    fun getCartCount() {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.getCartCount(object : ICNewApiListener<ICResponse<Int>> {
                override fun onSuccess(obj: ICResponse<Int>) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    cartCount.postValue(obj.data)
                }

                override fun onError(error: ICResponseCode?) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                }
            })
        }
    }

    fun getHistoryByKey(key: String, isLoadMore: Boolean = false, isSearch: Boolean = false) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            if (!isLoadMore) {
                offset = 0
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.getListScanHistoryBySearch(offset, null, null,null, key, object : ICNewApiListener<ICResponse<ICListResponse<ICItemHistory>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICItemHistory>>) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    if (obj.data?.rows != null && !obj.data?.rows.isNullOrEmpty()) {
                        offset += APIConstants.LIMIT
                        if (!isLoadMore) {
                            isLoadMoreData.postValue(true)
                        }
                        listData.postValue(obj.data?.rows)
                    } else {
                        if (!isLoadMore) {
                            if (isSearch) {
                                errorData.postValue(Constant.ERROR_EMPTY_SEARCH)
                            } else {
                                errorData.postValue(Constant.ERROR_EMPTY)
                            }
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
package vn.icheck.android.screen.user.store_sell_history

import android.content.Intent
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
import vn.icheck.android.network.models.history.ICStoreNear

class StoreSellHistoryViewModel : ViewModel() {

    private val interactor = HistoryInteractor()

    val dataProduct = MutableLiveData<ICItemHistory>()
    val listData = MutableLiveData<MutableList<ICStoreNear>>()
    val isLoadMoreData = MutableLiveData<Boolean>()
    val errorData = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val onError = MutableLiveData<Int>()

    var idProduct: Long? = null

    private var offset = 0

    fun getDataIntent(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICItemHistory
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            idProduct = obj.product?.sourceId!!
            dataProduct.postValue(obj)
            getStoreSell(idProduct!!)
        } else {
            onError.postValue(Constant.ERROR_UNKNOW)
        }
    }

    fun getStoreSell(idProduct: Long, isLoadMore: Boolean = false) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            if (!isLoadMore) {
                statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
                offset = 0
            }

            interactor.getStoreSell(idProduct, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICStoreNear>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICStoreNear>>) {
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
                        } else {
                            listData.postValue(mutableListOf())
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
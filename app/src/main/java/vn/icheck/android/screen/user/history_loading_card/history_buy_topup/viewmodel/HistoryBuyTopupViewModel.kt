package vn.icheck.android.screen.user.history_loading_card.history_buy_topup.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.recharge_phone.RechargePhoneInteractor
import vn.icheck.android.network.models.ICNone
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone

class HistoryBuyTopupViewModel : ViewModel() {

    private val interactor = RechargePhoneInteractor()
    private var offset = 0

    val listDataBuyTopup = MutableLiveData<MutableList<ICRechargePhone>>()
    val listDataLoadedTopup = MutableLiveData<MutableList<ICRechargePhone>>()
    val isLoadMoreData = MutableLiveData<Boolean>()
    val errorData = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    val messageError = MutableLiveData<String>()

    fun onGetDataBuyTopup(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.getHistoryBuyTopupV2(offset, object : ICNewApiListener<ICResponse<ICListResponse<ICRechargePhone>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICRechargePhone>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                if (obj.data?.rows != null && !obj.data?.rows.isNullOrEmpty()) {
                    offset += APIConstants.LIMIT
                    if (!isLoadMore) {
                        isLoadMoreData.postValue(true)
                    }
                    listDataBuyTopup.postValue(obj.data?.rows)
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

    fun onGetDataLoadedTopup(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        interactor.getHistoryLoadedTopupV2(offset, object : ICNewApiListener<ICResponse<ICListResponse<ICRechargePhone>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICRechargePhone>>) {
                if (obj.data?.rows != null && !obj.data?.rows.isNullOrEmpty()) {
                    offset += APIConstants.LIMIT
                    if (!isLoadMore) {
                        isLoadMoreData.postValue(true)
                    }
                    listDataLoadedTopup.postValue(obj.data?.rows)
                } else {
                    if (!isLoadMore) {
                        errorData.postValue(Constant.ERROR_EMPTY)
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                errorData.postValue(Constant.ERROR_SERVER)
            }
        })
    }
}
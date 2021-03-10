package vn.icheck.android.screen.user.pvcombank.specialoffer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.pvcombank.PVcomBankRepository
import vn.icheck.android.network.models.pvcombank.ICSpecialOfferCardPVBank

class SpecialOfferPVCardViewModel : ViewModel() {

    private val interactor = PVcomBankRepository()
    private var offset = 0

    val listData = MutableLiveData<MutableList<ICSpecialOfferCardPVBank>>()
    val isLoadMoreData = MutableLiveData<Boolean>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val errorData = MutableLiveData<Int>()

    fun onGetData(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.getSpecialOffer(offset, object : ICNewApiListener<ICResponse<ICListResponse<ICSpecialOfferCardPVBank>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICSpecialOfferCardPVBank>>) {
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
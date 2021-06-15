package vn.icheck.android.screen.user.pvcombank.listcard.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.base.Result
import vn.icheck.android.network.feature.pvcombank.PVcomBankRepository
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.network.models.pvcombank.ICLockCard

class ListCardPVComBankViewModel : BaseViewModel() {

    private val interactor = PVcomBankRepository()

    var cardId = ""
    var pos = -1

    val errorData = MutableLiveData<Int>()
    val errorString = MutableLiveData<String>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    val listData = MutableLiveData<MutableList<ICListCardPVBank>>()
    val dataLockCard = MutableLiveData<ICLockCard>()
    val defaultCard = MutableLiveData<Boolean>()

    fun getListCards(): LiveData<Result<ICResponse<ICListResponse<ICListCardPVBank>>?>> {
        interactor.dispose()
        return request { interactor.getMyListCards() }
    }

    fun getKyc() = request { interactor.getKyc() }

//    fun getData() {
//        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
//            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
//            return
//        }
//
//        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
//
//        interactor.getMyListCard(object : ICNewApiListener<ICResponse<ICListResponse<ICListCardPVBank>>> {
//            override fun onSuccess(obj: ICResponse<ICListResponse<ICListCardPVBank>>) {
//                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
//                if (!obj.data?.rows.isNullOrEmpty()) {
//                    listData.postValue(obj.data?.rows)
//                } else {
//                    errorData.postValue(Constant.ERROR_EMPTY)
//                }
//            }
//
//            override fun onError(error: ICResponseCode?) {
//                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
//                errorData.postValue(Constant.ERROR_SERVER)
//            }
//        })
//    }

    fun lockCard(cardId: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (cardId.isNullOrEmpty()) {
            errorString.postValue(ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.lockCard(cardId, object : ICNewApiListener<ICResponse<ICLockCard>> {
            override fun onSuccess(obj: ICResponse<ICLockCard>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null) {
                    obj.data?.let {
                        dataLockCard.postValue(it)
                    }
                } else {
                    errorString.postValue(obj.message
                            ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorString.postValue(error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun setDefaultCard(cardId: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (cardId.isNullOrEmpty()) {
            errorString.postValue(ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.setDefaultCard(cardId, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null && obj.data == true) {
                    defaultCard.postValue(true)
                } else {
                    errorString.postValue(obj.message
                            ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorString.postValue(error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }


}
package vn.icheck.android.screen.user.pvcombank.confirmlockcard.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.pvcombank.PVcomBankRepository
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.network.models.pvcombank.ICLockCard

class ConfirmLockPVCardViewModel : ViewModel() {

    private val interactor = PVcomBankRepository()

    var objCard: ICListCardPVBank? = null

    var requestId = ""
    var otptranid = ""

    val dataLockCard = MutableLiveData<ICLockCard>()
    val errorData = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    fun getDataIntent(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICListCardPVBank
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            objCard = obj
            lockCard(objCard?.cardId)
        }
    }

    fun lockCard(cardId: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (cardId.isNullOrEmpty()) {
            errorData.postValue(Constant.ERROR_UNKNOW)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.lockCard(cardId, object : ICNewApiListener<ICResponse<ICLockCard>>{
            override fun onSuccess(obj: ICResponse<ICLockCard>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null){
                    obj.data?.let {
                        dataLockCard.postValue(it)
                    }
                } else {
                    errorData.postValue(Constant.ERROR_EMPTY)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(Constant.ERROR_SERVER)
            }
        })
    }

    fun verifyPinCard(pin: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.verifyPin(pin, object : ICNewApiListener<ICResponse<ICLockCard>>{
            override fun onSuccess(obj: ICResponse<ICLockCard>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null){
                    // chưa có api verify mã pin
                } else {
                    errorData.postValue(Constant.ERROR_EMPTY)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(Constant.ERROR_SERVER)
            }
        })
    }


}
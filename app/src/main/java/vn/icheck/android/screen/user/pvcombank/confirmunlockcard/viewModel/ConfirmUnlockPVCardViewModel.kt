package vn.icheck.android.screen.user.pvcombank.confirmunlockcard.viewModel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.pvcombank.PVcomBankRepository
import vn.icheck.android.network.models.pvcombank.ICInfoPVCard
import vn.icheck.android.network.models.pvcombank.ICListCardPVBank
import vn.icheck.android.network.models.pvcombank.ICLockCard

class ConfirmUnlockPVCardViewModel : ViewModel() {

    private val interactor = PVcomBankRepository()

    var objCard: ICListCardPVBank? = null

    var requestId = ""
    var otptranid = ""
    var fullcard: String? = null

    val dataUnLockCard = MutableLiveData<ICLockCard>()
    val unlockCardSuccess = MutableLiveData<String>()
    val errorData = MutableLiveData<String>()
    val verifyError = MutableLiveData<String>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    fun getData(intent: Intent?) {
        val obj = try {
            intent?.getSerializableExtra(Constant.DATA_1) as ICListCardPVBank
        } catch (e: Exception) {
            null
        }

        val type = intent?.getStringExtra(Constant.DATA_2) ?: ""

        if (obj != null) {
            objCard = obj

            when (type) {
                "full_card" -> {
                    showFullCard(objCard?.cardId)
                }
                else -> {
                    unlockCard(objCard?.cardId)
                }
            }
        }
    }

    fun unlockCard(cardId: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (cardId.isNullOrEmpty()) {
            errorData.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.unlockCard(cardId, object : ICNewApiListener<ICResponse<ICLockCard>> {
            override fun onSuccess(obj: ICResponse<ICLockCard>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null) {
                    fullcard = obj.data?.fullCard
                    obj.data?.let {
                        dataUnLockCard.postValue(it)
                    }
                } else {
                    errorData.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(error?.message?:ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun showFullCard(cardId: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (cardId.isNullOrEmpty()) {
            errorData.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.getFullCard(cardId, object : ICNewApiListener<ICResponse<ICLockCard>> {
            override fun onSuccess(obj: ICResponse<ICLockCard>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data != null) {
                    obj.data?.let {
                        dataUnLockCard.postValue(it)
                    }
                } else {
                    errorData.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(error?.message?:ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun verifyOtpCard(otp: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.verifyOtp(requestId, otp, otptranid, object : ICNewApiListener<ICResponse<ICInfoPVCard>> {
            override fun onSuccess(obj: ICResponse<ICInfoPVCard>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (!obj.data?.fullCard.isNullOrEmpty()) {
                    obj.data?.fullCard?.let {
                        unlockCardSuccess.postValue(it)
                    }
                } else {
                    verifyError.postValue(obj.data?.message?:ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                verifyError.postValue(error?.message?:ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }
}
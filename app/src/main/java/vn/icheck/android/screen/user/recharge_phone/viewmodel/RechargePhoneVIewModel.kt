package vn.icheck.android.screen.user.recharge_phone.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.campaign.ListCampaignInteractor
import vn.icheck.android.network.feature.recharge_phone.RechargePhoneInteractor
import vn.icheck.android.network.models.ICRechargeThePhone
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone

class RechargePhoneVIewModel : ViewModel() {

    private val interactor = RechargePhoneInteractor()

    val data = MutableLiveData<MutableList<ICRechargePhone>>()

    val errorData = MutableLiveData<String>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val errorType = MutableLiveData<Int>()

    fun getData(type: Int) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.getDataTopupV2(object : ICNewApiListener<ICResponse<ICRechargeThePhone>> {
                override fun onSuccess(obj: ICResponse<ICRechargeThePhone>) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                    if (type == 1) {
                        if (!obj.data?.phoneTopup.isNullOrEmpty()) {
                            data.postValue(obj.data?.phoneTopup)
                        } else {
                            errorType.postValue(Constant.ERROR_EMPTY)
                        }
                    } else {
                        if (!obj.data?.phoneCard.isNullOrEmpty()) {
                            data.postValue(obj.data?.phoneCard)
                        } else {
                            errorType.postValue(Constant.ERROR_EMPTY)
                        }
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    val message = error?.message
                            ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    errorData.postValue(message)
                }
            })
        }
    }
}
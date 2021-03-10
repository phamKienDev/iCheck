package vn.icheck.android.screen.user.payment_topup.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.model.ICNameValue
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.recharge_phone.RechargePhoneInteractor
import vn.icheck.android.network.feature.setting.SettingRepository
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.network.util.JsonHelper

class PaymentViewModel : BaseViewModel() {

    private val interactor = RechargePhoneInteractor()
    private val icoinOfMe = SettingRepository()

    var dataIntent = MutableLiveData<ICPaymentLocal>()
    val dataListPaymentType = MutableLiveData<MutableList<ICRechargePhone>>()
    val dataBuyCard = MutableLiveData<ICPaymentLocal>()
    val errorData = MutableLiveData<String>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val errorType = MutableLiveData<Int>()

    val detailCard = MutableLiveData<ICRechargePhone>()
    val errorMessage = MutableLiveData<String>()

    fun getDataIntent(intent: Intent) {
        var card: ICRechargePhone? = null

        val type = intent.getIntExtra("type", 0)
        val value = intent.getStringExtra("value")
//        val idValue = intent.getLongExtra("serviceId", -1L)
        intent.getStringExtra("card")?.let {
            card = JsonHelper.parseJson(it, ICRechargePhone::class.java)
        }
        val phoneNumber = intent.getStringExtra("phone")

        val listValue = mutableListOf<ICNameValue>()
        if (type == 1) {
            listValue.add(ICNameValue("Dịch vụ:", "Nạp thẻ điện thoại"))
        } else {
            listValue.add(ICNameValue("Dịch vụ:", "Mua mã thẻ điện thoại"))
        }
        listValue.add(ICNameValue("Nhà mạng:", card?.provider))
        if (type == 1) {
            if (phoneNumber.isNullOrEmpty()) {
                val replacePhone = SessionManager.session.user?.phone?.let {
                    StringBuilder(it).apply {
                        replace(0, 2, "0")
                    }.toString()
                }
                listValue.add(ICNameValue("Nạp cho số:", replacePhone))
            } else {
                listValue.add(ICNameValue("Nạp cho số:", phoneNumber))
            }
        }
        listValue.add(ICNameValue("Mệnh giá:", TextHelper.formatMoneyPhay(value.toLong()) + "đ"))
        listValue.add(ICNameValue("Phí dịch vụ:", "Miễn phí"))

//        dataIntent.postValue(ICPaymentLocal(value, card?.serviceId, card, listValue, phoneNumber, type))
        dataIntent.postValue(ICPaymentLocal(value, card, listValue, phoneNumber, type))
    }

    fun getListPaymentType() {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.getListPaymentType(object : ICNewApiListener<ICResponse<ICListResponse<ICRechargePhone>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICRechargePhone>>) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        dataListPaymentType.postValue(obj.data?.rows)
                    } else {
                        errorType.postValue(Constant.ERROR_EMPTY)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    postError(error?.message)
                }
            })
        }
    }

    fun buyTopup(mType: Int?, serviceId: Long, amount: Long, payType: String) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.buyCard(serviceId, amount, payType, object : ICNewApiListener<ICResponse<ICRechargePhone>> {
                override fun onSuccess(obj: ICResponse<ICRechargePhone>) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    if (obj.data != null && obj.statusCode == "200") {
                        dataBuyCard.postValue(ICPaymentLocal(null, obj.data, null, null, mType))
                    } else {
                        errorData.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    postError(error?.message)
                }
            })
        }
    }

    fun rechargeCard(mType: Int?, serviceId: Long, amount: Long, payType: String, phone: String) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.rechargeCard(serviceId, amount, payType, phone, object : ICNewApiListener<ICResponse<ICRechargePhone>> {
                override fun onSuccess(obj: ICResponse<ICRechargePhone>) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    if (obj.data != null && obj.statusCode == "200") {
                        dataBuyCard.postValue(ICPaymentLocal(null, obj.data, null, null, mType))
                    } else {
                        errorData.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                    if (error?.statusCode != "S50002") {
                        postError(error?.message)
                    } else {
                        errorData.postValue("Thông tin nhà mạng không đúng.\nVui lòng liên hệ với iCheck để được hỗ trợ!")
                    }
                }
            })
        }
    }

    fun vnpayCard(payType: String, mType: Int?, amount: Long, returnUrl: String, phone: String?, serviceId: Long, orderType: String) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
                return@launch
            }

            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.vnpayCard(payType, returnUrl, amount, phone, serviceId, orderType, object : ICNewApiListener<ICResponse<ICRechargePhone>> {
                override fun onSuccess(obj: ICResponse<ICRechargePhone>) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                    dataBuyCard.postValue(ICPaymentLocal(null, obj.data, null, null, mType))
                }

                override fun onError(error: ICResponseCode?) {
                    postError(error?.message)
                }
            })
        }
    }

    fun getDetailCard(orderId: Long) {
        viewModelScope.launch {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                errorMessage.postValue(ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                return@launch
            }

            interactor.getDetailCard(orderId, object : ICNewApiListener<ICResponse<ICRechargePhone>> {
                override fun onSuccess(obj: ICResponse<ICRechargePhone>) {
                    detailCard.postValue(obj.data)
                }

                override fun onError(error: ICResponseCode?) {
                    if (error?.statusCode != "S50002"){
                        errorMessage.postValue(error?.message)
                    }else{
                        errorMessage.postValue("Thông tin nhà mạng không đúng.\nVui lòng liên hệ với iCheck để được hỗ trợ!")
                    }
                }
            })
        }
    }

    fun postError(message: String?) {
        statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
        val message = message
                ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
        errorData.postValue(message)
    }

    fun getCoin() = request { icoinOfMe.getCoin() }
}
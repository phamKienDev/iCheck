package vn.icheck.android.screen.user.payment_topup.viewmodel

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.model.ICNameValue
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
            listValue.add(ICNameValue(getString(R.string.dich_vu_colon), getString(R.string.nap_the_dien_thoai)))
        } else {
            listValue.add(ICNameValue(getString(R.string.dich_vu_colon), getString(R.string.mua_ma_the_dien_thoai)))
        }
        listValue.add(ICNameValue(getString(R.string.nha_mang_colon), card?.provider))
        if (type == 1) {
            if (phoneNumber.isNullOrEmpty()) {
                val replacePhone = SessionManager.session.user?.phone?.let {
                    StringBuilder(it).apply {
                        replace(0, 2, "0")
                    }.toString()
                }
                listValue.add(ICNameValue(getString(R.string.nap_cho_so_colon), replacePhone))
            } else {
                listValue.add(ICNameValue(getString(R.string.nap_cho_so_colon), phoneNumber))
            }
        }
        listValue.add(ICNameValue(getString(R.string.menh_gia), getString(R.string.s_d, TextHelper.formatMoneyPhay(value?.toLong()))))
        listValue.add(ICNameValue(getString(R.string.phi_dich_vu_colon), getString(R.string.mien_phi)))

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
                        errorData.postValue( getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
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
                        errorData.postValue( getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                    if (error?.statusCode != "S50002") {
                        postError(error?.message)
                    } else {
                        errorData.postValue(getString(R.string.thong_tin_khong_dung_vui_long_lien_he_voi_icheck_de_duoc_ho_tro))
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
                errorMessage.postValue( getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                return@launch
            }

            interactor.getDetailCard(orderId, object : ICNewApiListener<ICResponse<ICRechargePhone>> {
                override fun onSuccess(obj: ICResponse<ICRechargePhone>) {
                    obj.data?.let {
                        detailCard.postValue(it)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    if (error?.statusCode != "S50002"){
                        errorMessage.postValue(error?.message)
                    }else{
                        errorMessage.postValue(getString(R.string.thong_tin_khong_dung_vui_long_lien_he_voi_icheck_de_duoc_ho_tro))
                    }
                }
            })
        }
    }

    fun postError(message: String?) {
        statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
        val message = message
                ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
        errorData.postValue(message)
    }

    fun getCoin() = request { icoinOfMe.getCoin() }
}
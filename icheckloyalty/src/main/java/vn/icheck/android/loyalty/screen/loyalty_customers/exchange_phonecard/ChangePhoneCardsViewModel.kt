package vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository

class ChangePhoneCardsViewModel : BaseViewModel<Any>() {
    private val repository = LoyaltyCustomersRepository()

    val showErrorDialog = MutableLiveData<String>()
    val showDialogError = MutableLiveData<String>()

    val onTopUpServiceSuccess = MutableLiveData<List<TopupServices.Service>>()
    val onExchangeSuccess = MutableLiveData<ICKRedemptionHistory>()

    private var typeGift = ConstantsLoyalty.TDDH
    private var campaignId: Long? = null

    fun getDataIntent(intent: Intent?) {
        collectionID = try {
            intent?.getLongExtra(ConstantsLoyalty.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        typeGift = intent?.getStringExtra(ConstantsLoyalty.DATA_2) ?: ConstantsLoyalty.TDDH
        campaignId = intent?.getLongExtra(ConstantsLoyalty.DATA_3, -1)

        if (collectionID != -1L) {
            getTopUpService()
        } else {
            showErrorDialog.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        }
    }

    private fun getTopUpService() {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            showErrorDialog.postValue(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        repository.dispose()

        repository.getTopUpService(object : ICApiListener<ICKResponse<TopupServiceResponse>> {
            override fun onSuccess(obj: ICKResponse<TopupServiceResponse>) {
                obj.data?.phoneTopup?.let {
                    onTopUpServiceSuccess.postValue(it)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                showErrorDialog.postValue(error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun exchangeGift(phone: String, serviceId: Long) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        when (typeGift) {
            ConstantsLoyalty.TDDH -> {
                repository.exchangeGift(collectionID, phone, serviceId, object : ICApiListener<ICKResponse<ICKRedemptionHistory>> {
                    override fun onSuccess(obj: ICKResponse<ICKRedemptionHistory>) {
                        if (obj.statusCode == 200) {
                            obj.data?.let {
                                onExchangeSuccess.postValue(it)
                            }
                        } else {
                            showDialogError.postValue(obj.data?.message
                                    ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                        }
                    }

                    override fun onError(error: ICKBaseResponse?) {
                        checkError(true, error?.message)
                    }
                })
            }
            ConstantsLoyalty.TDNH -> {
                if (campaignId != null) {
                    repository.exchangeCardGiftTDNH(campaignId!!, collectionID, serviceId, phone, object : ICApiListener<ICKResponse<ICKRedemptionHistory>> {
                        override fun onSuccess(obj: ICKResponse<ICKRedemptionHistory>) {
                            if (obj.statusCode == 200) {
                                obj.data?.let {
                                    onExchangeSuccess.postValue(it)
                                }
                            } else {
                                showDialogError.postValue(obj.data?.message
                                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                            }
                        }

                        override fun onError(error: ICKBaseResponse?) {
                            checkError(true, error?.message)
                        }

                    })
                }
            }
            else -> {
                repository.exchangeCardGiftVQMM(serviceId, collectionID, phone, object : ICApiListener<ICKResponse<ICKRedemptionHistory>> {
                    override fun onSuccess(obj: ICKResponse<ICKRedemptionHistory>) {
                        if (obj.statusCode == 200) {
                            obj.data?.let {
                                onExchangeSuccess.postValue(it)
                            }
                        } else {
                            showDialogError.postValue(obj.data?.message
                                    ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                        }
                    }

                    override fun onError(error: ICKBaseResponse?) {
                        checkError(true, error?.message)
                    }
                })

            }
        }

    }
}
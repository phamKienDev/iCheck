package vn.icheck.android.loyalty.screen.gift_detail_from_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.ToastHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.CampaignRepository
import vn.icheck.android.loyalty.repository.RedeemPointRepository
import java.lang.Exception

class GiftDetailFromAppViewModel : BaseViewModel<Any>() {
    private val repository = CampaignRepository()
    private val exchangeRepository = RedeemPointRepository()

    val onErrorString = MutableLiveData<String>()
    val onSuccess = MutableLiveData<ICKGift>()

    val onActionSuccess = MutableLiveData<String>()

    fun getDataIntent(intent: Intent?) {
        collectionID = try {
            intent?.getLongExtra(ConstantsLoyalty.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        if (collectionID != -1L) {
            getDetailGiftWinner()
        } else {
            onErrorString.postValue("")
        }
    }

    private fun getDetailGiftWinner() {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        repository.dispose()
        repository.getDetailGiftWinner(collectionID, object : ICApiListener<ICKResponse<ICKGift>> {
            override fun onSuccess(obj: ICKResponse<ICKGift>) {
                if (obj.statusCode == 200) {
                    obj.data?.let {
                        onSuccess.postValue(it)
                    }
                } else {
                    checkError(true, getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message
                        ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    fun postCancelShipGift(data: ICKGift) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            ToastHelper.showLongError(ApplicationHelper.getApplicationByReflect(), getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (data.id == null) {
            ToastHelper.showLongError(ApplicationHelper.getApplicationByReflect(), getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            return
        }

        CampaignRepository().postCancelShipGift(data.id, object : ICApiListener<ICKResponse<ICKWinner>> {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(obj: ICKResponse<ICKWinner>) {
                if (obj.statusCode == 200) {
                    onActionSuccess.postValue("")
                } else {
                    ToastHelper.showLongError(ApplicationHelper.getApplicationByReflect(), getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                ToastHelper.showLongError(ApplicationHelper.getApplicationByReflect(), getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }
}
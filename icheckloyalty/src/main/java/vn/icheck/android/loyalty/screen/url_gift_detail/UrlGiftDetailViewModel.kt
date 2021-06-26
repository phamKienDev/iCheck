package vn.icheck.android.loyalty.screen.url_gift_detail

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.CampaignRepository

class UrlGiftDetailViewModel : BaseViewModel<Any>() {
    private val repository = CampaignRepository()

    val onSuccess = MutableLiveData<ICKLoyalty>()

    var barcode = ""

    var obj: ICKLoyalty? = null

    var code = ""

    fun getDataIntent(intent: Intent?) {
        collectionID = try {
            intent?.getLongExtra(ConstantsLoyalty.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        barcode = try {
            intent?.getStringExtra(ConstantsLoyalty.DATA_2) ?: ""
        } catch (e: Exception) {
            ""
        }

        obj = try {
            intent?.getSerializableExtra(ConstantsLoyalty.DATA_3) as ICKLoyalty?
        } catch (e: Exception) {
            null
        }

        if (obj != null) {
            onSuccess.postValue(obj!!)
        } else {
            getDetailGift()
        }
    }

    private fun getDetailGift() {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        repository.dispose()
        repository.getGiftDetail(collectionID, object : ICApiListener<ICKResponse<ICKLoyalty>> {
            override fun onSuccess(obj: ICKResponse<ICKLoyalty>) {
                obj.data?.let {
                    onSuccess.postValue(it)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}
package vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKCampaignOfBusiness
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository

class LoyaltyVipDetailViewModel : BaseViewModel<Any>() {
    private val repository = LoyaltyCustomersRepository()

    val onSuccess = MutableLiveData<ICKCampaignOfBusiness>()

    fun getCampaignDetailLongTime(){
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())){
            checkError(false)
            return
        }

        repository.getCampaignDetailLongTime(collectionID, object : ICApiListener<ICKResponse<ICKCampaignOfBusiness>> {
            override fun onSuccess(obj: ICKResponse<ICKCampaignOfBusiness>) {
                onSuccess.postValue(obj.data)
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}
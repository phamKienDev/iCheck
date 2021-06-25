package vn.icheck.android.loyalty.screen.loyalty_customers.giftdetail

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKRedemptionHistory
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository

class GiftDetailViewModel : BaseViewModel<Any>() {
    private val repository = LoyaltyCustomersRepository()

    val setData = MutableLiveData<ICKRedemptionHistory>()

    fun getDetailGift(){
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())){
            checkError(false)
            return
        }

        repository.dispose()
        repository.getDetailGift(collectionID, object : ICApiListener<ICKResponse<ICKRedemptionHistory>> {
            override fun onSuccess(obj: ICKResponse<ICKRedemptionHistory>) {
                obj.data?.let {
                    setData.postValue(it)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }

    fun getDetailGiftStore(){
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())){
            checkError(false)
            return
        }

        repository.dispose()
        repository.getDetailGiftStore(collectionID, object : ICApiListener<ICKResponse<ICKRedemptionHistory>> {
            override fun onSuccess(obj: ICKResponse<ICKRedemptionHistory>) {
                obj.data?.let {
                    setData.postValue(it)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}
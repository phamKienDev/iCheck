package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.detail_gift_point

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKBoxGifts
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository

class DetailGiftLoyaltyViewModel: BaseViewModel<Any>() {
    private val repository = RedeemPointRepository()

    val onSuccess = MutableLiveData<ICKBoxGifts>()

    fun getDetailGift(){
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        repository.getDetailGift(collectionID, object : ICApiListener<ICKResponse<ICKBoxGifts>> {
            override fun onSuccess(obj: ICKResponse<ICKBoxGifts>) {
                if (obj.statusCode != 200){
                    checkError(true, obj.data?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }else{
                    onSuccess.postValue(obj.data)
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true)
            }
        })
    }
}
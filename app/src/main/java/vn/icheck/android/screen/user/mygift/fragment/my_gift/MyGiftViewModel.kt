package vn.icheck.android.screen.user.mygift.fragment.my_gift

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.campaign.CampainsInteractor
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.screen.user.mygift.fragment.reward_item_v2.RewardItemModel

class MyGiftViewModel : ViewModel() {
    val rewardItem = CampainsInteractor()

    var offset = 0

    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<RewardItemModel>()

    fun getListMyGiftBox(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        rewardItem.getListMyGiftBox(offset, object : ICNewApiListener<ICResponse<ICListResponse<ICItemReward>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICItemReward>>) {
                if (!obj.data?.rows.isNullOrEmpty()) {
                    offset += APIConstants.LIMIT
                    liveData.postValue(RewardItemModel(obj.data?.rows!!, isLoadMore, obj.data?.count!!))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}
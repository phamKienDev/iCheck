package vn.icheck.android.screen.user.mygift.fragment.reward_item_v2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.campaign.CampainsInteractor
import vn.icheck.android.network.models.ICItemReward

class MyGiftsViewModel : ViewModel() {
    val rewardItem = CampainsInteractor()

    var offset = 0

    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<RewardItemModel>()
    var totalItems = 0

    fun getListRewardItem(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        rewardItem.getListRewardItem(offset, object : ICNewApiListener<ICResponse<ICListResponse<ICItemReward>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICItemReward>>) {
                if (offset == 0) {
                    totalItems = obj.data?.count ?: 0
                }
                offset += APIConstants.LIMIT
                liveData.postValue(RewardItemModel(obj.data?.rows
                        ?: mutableListOf(), isLoadMore, obj.data?.count ?: 0))
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}
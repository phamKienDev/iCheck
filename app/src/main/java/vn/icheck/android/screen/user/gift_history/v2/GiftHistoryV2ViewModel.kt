package vn.icheck.android.screen.user.gift_history.v2

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.constant.LOGO
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.campaign.CampainsInteractor
import vn.icheck.android.network.models.ICItemReward

class GiftHistoryV2ViewModel : BaseViewModel<ICItemReward>() {
    private val repository = CampainsInteractor()

    var campaignId = ""
    private var banner = ""

    val headerImage = MutableLiveData<String>()

    val errorNotId = MutableLiveData<String>()
    val errorEmpty = MutableLiveData<String>()

    fun getDataIntent(intent: Intent?) {
        campaignId = intent?.getStringExtra(Constant.DATA_1) ?: ""
        banner = intent?.getStringExtra(LOGO) ?: ""

        if (campaignId.isEmpty()) {
            errorNotId.postValue(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
        } else {
            headerImage.postValue(banner)
            getGiftHistory()
        }
    }

    fun getGiftHistory(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            checkError(false)
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getGiftHistory(campaignId, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICItemReward>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICItemReward>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        errorEmpty.postValue("Error")
                    } else {
                        onSetData.postValue(obj.data?.rows)
                    }
                } else {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICResponseCode?) {
                checkError(true, error?.message)
            }
        })
    }
}
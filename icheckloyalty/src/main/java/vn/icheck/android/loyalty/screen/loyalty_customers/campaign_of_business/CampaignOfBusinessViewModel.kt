package vn.icheck.android.loyalty.screen.loyalty_customers.campaign_of_business

import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKCampaignOfBusiness
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository

class CampaignOfBusinessViewModel : BaseViewModel<ICKCampaignOfBusiness>() {
    private val repository = LoyaltyCustomersRepository()

    fun getCampaignOfBusiness(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getCampaignOfBusiness(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKCampaignOfBusiness>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKCampaignOfBusiness>>) {
                offset += APIConstants.LIMIT
                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        setErrorEmpty(R.drawable.ic_error_emty_history_topup, "Chưa có chương trình nào đang diễn ra", "Thường xuyên theo dõi các chương trình\nđể có cơ hội nhận các phần quà hấp dẫn", "", 0, R.color.white)
                    } else {
                        onSetData.postValue(obj.data?.rows)
                    }
                } else {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}
package vn.icheck.android.loyalty.screen.redemption_history

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.*
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository
import vn.icheck.android.loyalty.repository.RedeemPointRepository

class RedemptionHistoryViewModel : BaseViewModel<ICKRewardGameLoyalty>() {
    private val repositoryICKRewardGameLoyalty = RedeemPointRepository()
    private val repositoryICKRedemptionHistory = LoyaltyCustomersRepository()

    val setData = MutableLiveData<MutableList<ICKRedemptionHistory>>()
    val addData = MutableLiveData<MutableList<ICKRedemptionHistory>>()

    fun getRedemptionHistory(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repositoryICKRewardGameLoyalty.dispose()
        }

        repositoryICKRewardGameLoyalty.getListOfGiftsReceived(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKRewardGameLoyalty>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKRewardGameLoyalty>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        setErrorEmpty(R.drawable.ic_point_loyalty_empty, "Bạn chưa có lịch sử đổi quà", "Tham gia tích điểm để đổi quà to nào!", "Tích điểm ngay", R.drawable.bg_gradient_button_orange_yellow, R.color.white)
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

    fun getRedemptionHistoryLongTime(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repositoryICKRedemptionHistory.dispose()
        }

        repositoryICKRedemptionHistory.getRedemptionHistoryLongTime(offset, collectionID, object : ICApiListener<ICKResponse<ICKListResponse<ICKRedemptionHistory>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKRedemptionHistory>>) {
                offset += APIConstants.LIMIT
                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        setErrorEmpty(R.drawable.ic_error_emty_history_topup, "Bạn chưa có quà nào", "Tới cửa hàng quà tặng để đổi\nnhững phần quà hấp dẫn nhé!", "Tới cửa hàng quà tặng", R.drawable.bg_gradient_button_blue, R.color.white)
                    } else {
                        setData.postValue(obj.data?.rows)
                    }
                } else {
                    addData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICKBaseResponse?) {
                checkError(true, error?.message)
            }
        })
    }
}
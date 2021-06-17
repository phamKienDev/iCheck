package vn.icheck.android.loyalty.screen.redemption_history

import androidx.lifecycle.MutableLiveData
import vn.icheck.android.ichecklibs.util.RStringUtils.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
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
                        setErrorEmpty(R.drawable.ic_point_loyalty_empty, rText(R.string.ban_chua_co_lich_su_doi_qua), rText(R.string.tham_gia_tich_diem_de_doi_qua_to_nao), rText(R.string.tich_diem_ngay), R.drawable.bg_gradient_button_orange_yellow, R.color.white)
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
                        setErrorEmpty(R.drawable.ic_error_emty_history_topup, rText(R.string.ban_chua_co_qua_nao), rText(R.string.toi_cua_hang_qua_tang_de_doi_nhung_phan_qua_hap_dan_nhe), rText(R.string.toi_cua_hang_qua_tang), R.drawable.bg_gradient_button_blue, R.color.white)
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
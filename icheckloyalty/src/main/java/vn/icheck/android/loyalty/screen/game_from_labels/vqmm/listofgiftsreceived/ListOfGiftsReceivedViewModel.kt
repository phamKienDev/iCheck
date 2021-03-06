package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.listofgiftsreceived

import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.model.ICKRewardGameVQMMLoyalty
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.VQMMRepository

class ListOfGiftsReceivedViewModel : BaseViewModel<ICKRewardGameVQMMLoyalty>() {
    private val repository = VQMMRepository()

    fun getListOfGiftsReceived(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getListOfGiftsReceived(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKRewardGameVQMMLoyalty>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKRewardGameVQMMLoyalty>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        if (SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect()).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM)){
                            setErrorEmpty(R.drawable.ic_default_loyalty,
                                vn.icheck.android.ichecklibs.util.getString(R.string.ban_chua_co_qua_nao),
                                vn.icheck.android.ichecklibs.util.getString(R.string.thu_nhap_ngay_biet_dau_van_may_toi),
                                vn.icheck.android.ichecklibs.util.getString(R.string.nhap_ma_ngay), R.drawable.bg_corner_53_no_solid_stroke_1_blue, R.color.colorSecondary)
                        }else{
                            setErrorEmpty(R.drawable.ic_default_loyalty,
                                vn.icheck.android.ichecklibs.util.getString(R.string.ban_chua_co_qua_nao),
                                vn.icheck.android.ichecklibs.util.getString(R.string.thu_quet_ngay_biet_dau_van_may_toi),
                                vn.icheck.android.ichecklibs.util.getString(R.string.quet_ma_ngay), R.drawable.bg_corner_53_no_solid_stroke_1_blue, R.color.colorSecondary)
                        }
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
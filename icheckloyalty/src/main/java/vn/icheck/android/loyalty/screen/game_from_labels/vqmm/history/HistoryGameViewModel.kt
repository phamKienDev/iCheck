package vn.icheck.android.loyalty.screen.game_from_labels.vqmm.history

import vn.icheck.android.ichecklibs.util.RStringUtils.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKItemReward
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.VQMMRepository

class HistoryGameViewModel : BaseViewModel<ICKItemReward>() {
    private val repository = VQMMRepository()

    fun getListCodeUsed(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getCodeUsed(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKItemReward>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKItemReward>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        setErrorEmpty(R.drawable.ic_default_loyalty, rText(R.string.ban_chua_nhap_ma_du_thuong_nao), rText(R.string.thu_nhap_ngay_van_may_biet_dau_toi), rText(R.string.nhap_ma_them_luot_quay), R.drawable.bg_corner_53_no_solid_stroke_1, R.color.white)
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

    fun getListScanCodeUsed(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getScanCodeUsed(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKItemReward>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKItemReward>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        setErrorEmpty(R.drawable.ic_default_loyalty, rText(R.string.ban_chua_quet_ma_qr_nao), rText(R.string.thu_quet_ngay_van_may_biet_dau_toi), rText(R.string.quet_ma_qr_them_luot_ngay), R.drawable.bg_corner_53_no_solid_stroke_1, R.color.white)
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
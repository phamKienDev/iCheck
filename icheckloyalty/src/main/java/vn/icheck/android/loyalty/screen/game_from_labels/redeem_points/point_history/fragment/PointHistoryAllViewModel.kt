package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.point_history.fragment

import vn.icheck.android.ichecklibs.util.RStringUtils.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICKPointHistory
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.RedeemPointRepository

class PointHistoryAllViewModel : BaseViewModel<ICKPointHistory>() {
    private val repository = RedeemPointRepository()

    fun getPointHistoryAll(isLoadMore: Boolean, target: String, type: String?) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getPointHistoryAll(collectionID, offset, target, type, object : ICApiListener<ICKResponse<ICKListResponse<ICKPointHistory>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKPointHistory>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        when (type) {
                            "accumulate-points" -> {
                                setErrorEmpty(R.drawable.ic_point_loyalty_empty, rText(R.string.ban_chua_co_lich_su_tich_diem), "", rText(R.string.tich_diem_ngay), R.drawable.bg_gradient_button_orange_yellow, R.color.white)
                            }
                            "exchange-gift" -> {
                                setErrorEmpty(R.drawable.ic_point_loyalty_empty, rText(R.string.ban_chua_co_lich_su_tieu_diem), "", rText(R.string.doi_qua_ngay), R.drawable.bg_gradient_button_orange_yellow, R.color.white)
                            }
                            else -> {
                                setErrorEmpty(R.drawable.ic_point_loyalty_empty, rText(R.string.ban_chua_co_lich_su), "", rText(R.string.tich_diem_ngay), R.drawable.bg_gradient_button_orange_yellow, R.color.white)
                            }
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
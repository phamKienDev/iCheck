package vn.icheck.android.loyalty.screen.loyalty_customers.loyaltyvipdetail.fragment.accumulationhistory

import vn.icheck.android.ichecklibs.util.RStringUtils.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.BaseViewModel
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.ApplicationHelper
import vn.icheck.android.loyalty.helper.NetworkHelper
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKBaseResponse
import vn.icheck.android.loyalty.model.ICKListResponse
import vn.icheck.android.loyalty.model.ICKPointHistory
import vn.icheck.android.loyalty.model.ICKResponse
import vn.icheck.android.loyalty.network.ICApiListener
import vn.icheck.android.loyalty.repository.LoyaltyCustomersRepository

class AccumulationHistoryViewModel : BaseViewModel<ICKPointHistory>() {
    private val repository = LoyaltyCustomersRepository()

    fun getAccumulationHistory(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ApplicationHelper.getApplicationByReflect())) {
            checkError(false)
            return
        }

        if (!isLoadMore) {
            offset = 0
            repository.dispose()
        }

        repository.getAccumulationHistory(collectionID, offset, object : ICApiListener<ICKResponse<ICKListResponse<ICKPointHistory>>> {
            override fun onSuccess(obj: ICKResponse<ICKListResponse<ICKPointHistory>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        if (SharedLoyaltyHelper(ApplicationHelper.getApplicationByReflect()).getBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_POINT_LONG_TIME)) {
                            setErrorEmpty(R.drawable.ic_error_emty_history_topup, rText(R.string.ban_chua_nhap_ma_nao), rText(R.string.nhap_ma_tren_san_pham_de_nhan_them_diem_thanh_vien_nhe), rText(R.string.nhap_ma_ngay), R.drawable.bg_corner_53_no_solid_stroke_1_blue, R.color.blueVip)
                        } else {
                            setErrorEmpty(R.drawable.ic_error_emty_history_topup, rText(R.string.ban_chua_quet_tem_nao), rText(R.string.quet_tem_qr_code_tren_san_pham_de_nhan_them_diem_thanh_vien_nhe), rText(R.string.quet_tem_ngay), R.drawable.bg_corner_53_no_solid_stroke_1_blue, R.color.blueVip)
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
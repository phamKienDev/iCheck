package vn.icheck.android.screen.user.history_accumulate_points.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.history.HistoryInteractor
import vn.icheck.android.network.models.ICHistoryPoint

class HistoryPointViewModel : ViewModel() {
    val interactorPoint = HistoryInteractor()

    val onSetData = MutableLiveData<MutableList<ICHistoryPoint>>()
    val onAddData = MutableLiveData<MutableList<ICHistoryPoint>>()
    val onError = MutableLiveData<ICError>()
    val onErrorEmpty = MutableLiveData<ICError>()

    var offset = 0

    fun getListPointUsed(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        interactorPoint.getListPointReceived(offset, 2, object : ICNewApiListener<ICResponse<ICListResponse<ICHistoryPoint>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICHistoryPoint>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        onSetData.postValue(obj.data?.rows)
                    }else{
                        onErrorEmpty.postValue(ICError(R.drawable.ic_group_120dp, getString(R.string.ban_chua_dung_diem_nao), getString(R.string.hay_tich_luy_va_su_dung_diem_de_doi_nhung_mon_qua_hap_dan_o_muc_dac_quyen_rieng_nhe), R.string.dung_diem))
                    }
                } else {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    fun getListPointReceived(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        interactorPoint.getListPointReceived(offset, 1, object : ICNewApiListener<ICResponse<ICListResponse<ICHistoryPoint>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICHistoryPoint>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        onSetData.postValue(obj.data?.rows)
                    }else{
                        onErrorEmpty.postValue(ICError(R.drawable.ic_group_120dp, "Bạn chưa dùng điểm nào", "Hãy tích lũy và sử dụng điểm để đổi những món quà hấp dẫn ở mục đặc quyền riêng nhé", R.string.dung_diem))
                    }
                } else {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }
}
package vn.icheck.android.screen.user.orderhistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.order.OrderInteractor
import vn.icheck.android.network.models.ICOrderHistoryV2

class OrderHistoryViewModel : ViewModel() {
    private val interactor = OrderInteractor()

    val onSetData = MutableLiveData<MutableList<ICOrderHistoryV2>>()
    val onAddData = MutableLiveData<MutableList<ICOrderHistoryV2>>()
    val onError = MutableLiveData<ICError>()

    var offset = 0

    // 0 - new - mới, 1 - pendingPayment - đợi thanh toán, 2 - processing - đợi xác nhận, 3 - confirmed - đã xác nhận,
    // 4 - shipping - đang giao hàng, 5 - completed - đã hoàn thành, 6 - cancelled - huỷ, 7 - closed - đóng/ lỗi thanh toán, 8 - refunded - hoàn tiền.
    fun getData(status: Int, isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }
        if (!isLoadmore)
            offset = 0

        interactor.getListOrders(status, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICOrderHistoryV2>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICOrderHistoryV2>>) {
                offset += APIConstants.LIMIT
                if (isLoadmore)
                    onAddData.postValue(obj.data?.rows)
                else
                    onSetData.postValue(obj.data?.rows)
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}
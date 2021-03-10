package vn.icheck.android.screen.user.bookmark

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.screen.user.listdistributor.BaseModelList

class BookMarkV2ViewModel : ViewModel() {
    private val bookmarkInteractor = ProductInteractor()

    var offset = 0

    val onError = MutableLiveData<ICError>()
    val liveData = MutableLiveData<BaseModelList<ICProductTrend>>()

    fun getListBookMark(isLoadMore: Boolean, search: String?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        bookmarkInteractor.getBookMark(offset, search, object : ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICProductTrend>>) {
                offset += APIConstants.LIMIT
                liveData.postValue(
                        BaseModelList(isLoadMore, obj.data?.rows ?: mutableListOf(), null, null))
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }
}
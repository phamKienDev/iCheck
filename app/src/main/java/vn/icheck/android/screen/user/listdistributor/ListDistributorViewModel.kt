package vn.icheck.android.screen.user.listdistributor

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICPage

class ListDistributorViewModel : ViewModel() {
    val listener = PageRepository()

    val onError = MutableLiveData<ICError>()
    val onSuccess = MutableLiveData<BaseModelList<ICPage>>()

    var offset = 0
    var url = ""

    fun getData(intent: Intent?) {
        url = try {
            intent?.getStringExtra(Constant.DATA_1) ?: ""
        }catch (e: Exception){
            ""
        }

        if (url.isNotEmpty()) {
            getListDistributor()
        } else {
            onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
        }
    }

    fun getListDistributor(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        listener.getListDistributor(url, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPage>>) {
                offset += APIConstants.LIMIT
                onSuccess.postValue(BaseModelList(isLoadMore, obj.data?.rows
                        ?: mutableListOf(), obj.data?.count, null))
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}
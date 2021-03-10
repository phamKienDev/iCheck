package vn.icheck.android.screen.user.list_product_history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.models.ICProductTrend

class ListProductHistoryViewModel : ViewModel() {

    val onError = MutableLiveData<ICError>()
    val onSetData = MutableLiveData<MutableList<ICProductTrend>>()
    val onAddData = MutableLiveData<MutableList<ICProductTrend>>()

    var offset = 0

    fun getData(isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadmore) {
            offset = 0
        }
        val list = mutableListOf<ICProductTrend>()
        for (i in 0 until 10) {
            list.add(ICProductTrend())
        }

        if (isLoadmore) {
            onAddData.postValue(list)
        }else{
            onSetData.postValue(list)
        }
    }
}
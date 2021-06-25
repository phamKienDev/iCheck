package vn.icheck.android.screen.user.listcontribute

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICContribute
import vn.icheck.android.screen.user.listdistributor.BaseModelList

class ListContributeViewModel : ViewModel() {
    val interaction = ProductInteractor()

    val liveData = MutableLiveData<BaseModelList<ICContribute>>()
    val onError = MutableLiveData<ICError>()

    var offset = 0
    var collectionID = ""

    fun getCollectionID(intent: Intent?) {
        collectionID = try {
            intent?.getStringExtra(Constant.DATA_1) ?: ""
        } catch (e: Exception) {
            ""
        }

        if (collectionID.isEmpty()) {
            onError.postValue(ICError(0, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
        } else {
            getListContribute(false)
        }
    }

    fun getListContribute(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        viewModelScope.launch {
            interaction.getContributor(collectionID, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICContribute>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICContribute>>) {
                    offset += APIConstants.LIMIT
                    liveData.postValue(BaseModelList(isLoadMore, obj.data?.rows ?: mutableListOf(), null, null))
                }

                override fun onError(error: ICResponseCode?) {
                    onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                }
            })
        }
    }
}
package vn.icheck.android.screen.user.newslistv2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.news.NewsInteractor
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.listdistributor.BaseModelList

class NewsListViewModel : ViewModel() {
    val listener = NewsInteractor()

    val liveData = MutableLiveData<BaseModelList<ICNews>>()
    var onError = MutableLiveData<ICError>()
    var offset = 0

    fun getNewsList(isLoadMore: Boolean) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        viewModelScope.launch {
            listener.getListNews(offset, APIConstants.LIMIT, object : ICNewApiListener<ICResponse<ICListResponse<ICNews>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICNews>>) {
                    offset += APIConstants.LIMIT
                    liveData.postValue(
                            BaseModelList(isLoadMore, obj.data?.rows ?: mutableListOf(), null, null)
                    )
                }

                override fun onError(error: ICResponseCode?) {
                    onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                }
            })
        }
    }
}
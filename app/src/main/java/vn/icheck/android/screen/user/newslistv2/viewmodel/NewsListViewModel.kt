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
import vn.icheck.android.network.models.ICArticleCategory
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.screen.user.listdistributor.BaseModelList

class NewsListViewModel : ViewModel() {
    val listener = NewsInteractor()
    val repository = NewsInteractor()

    val liveData = MutableLiveData<BaseModelList<ICNews>>()
    var onError = MutableLiveData<ICError>()
    var offset = 0

    val getCategorySuccess = MutableLiveData<MutableList<ICArticleCategory>>()

    fun getNewsList(isLoadMore: Boolean, articleCategoryId: Long? = null) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        listener.getListNews(offset, APIConstants.LIMIT, articleCategoryId, object : ICNewApiListener<ICResponse<ICListResponse<ICNews>>> {
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

    fun getCategoryNewsList() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        repository.getListNewsCategory(object : ICNewApiListener<ICResponse<ICListResponse<ICArticleCategory>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICArticleCategory>>) {
                obj.data?.rows?.add(0, ICArticleCategory().apply {
                    name = "Tất cả"
                    isChecked = true
                })
                getCategorySuccess.postValue(obj.data?.rows)
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}
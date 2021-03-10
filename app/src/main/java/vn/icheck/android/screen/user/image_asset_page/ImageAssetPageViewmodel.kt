package vn.icheck.android.screen.user.image_asset_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICMedia
import vn.icheck.android.network.models.ICMediaPage

class ImageAssetPageViewmodel : ViewModel() {

    private val pageInteractor = PageRepository()

    private var page = 0

    val listData = MutableLiveData<MutableList<ICMediaPage>>()
    val isLoadMoreData = MutableLiveData<Boolean>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()
    val errorData = MutableLiveData<Int>()

    fun onGetImagePage(idPage: Long,isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (!isLoadMore){
            page = 0
            statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
        }

        pageInteractor.getImageAssetsPage(page,idPage,object : ICNewApiListener<ICResponse<ICListResponse<ICMediaPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICMediaPage>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                if (obj.data?.rows != null && !obj.data?.rows.isNullOrEmpty()) {
                    page += APIConstants.LIMIT
                    if (!isLoadMore) {
                        isLoadMoreData.postValue(true)
                    }
                    listData.postValue(obj.data?.rows)
                } else {
                    errorData.postValue(Constant.ERROR_EMPTY)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(Constant.ERROR_SERVER)
            }
        })
    }
}
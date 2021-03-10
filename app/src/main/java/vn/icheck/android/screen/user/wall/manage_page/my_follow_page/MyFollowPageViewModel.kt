package vn.icheck.android.screen.user.wall.manage_page.my_follow_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICPage

class MyFollowPageViewModel : ViewModel() {
    val onSetData = MutableLiveData<MutableList<ICPage>>()
    val onAddData = MutableLiveData<MutableList<ICPage>>()
    val onState = MutableLiveData<ICMessageEvent>()

    private val interactor = PageRepository()
    private var offset = 0
    var countPage = 0

    fun getData(key: String, isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_NO_INTERNET))
            return
        }

        if (!isLoadmore) {
            offset = 0
        }

        interactor.getMyFollowPage(key,APIConstants.LIMIT, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPage>>) {
                offset += APIConstants.LIMIT
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.ON_CLOSE_LOADING))

                if (!isLoadmore) {
                    countPage = obj.data?.count ?: 0
                    onSetData.postValue(obj.data?.rows)
                } else{
                    onAddData.postValue(obj.data?.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onState.postValue(ICMessageEvent(ICMessageEvent.Type.MESSAGE_ERROR, error?.message
                        ?: ICheckApplication.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
            }
        })
    }
}
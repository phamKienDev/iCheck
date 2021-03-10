package vn.icheck.android.screen.user.user_follow_page

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.*

class UserFollowPageViewModel : ViewModel() {

    val onInfoData = MutableLiveData<ICPageOverview>()
    val onCountUserData = MutableLiveData<Int>()
    val onListUserData = MutableLiveData<MutableList<Any>>()
    val onError = MutableLiveData<ICError>()

    var offset = 0
    val interactor = PageRepository()
    var pageId: Long = -1L
    var pageOverview: ICPageOverview? = null

    fun getData(intent: Intent) {
        pageOverview = try {
            intent.getSerializableExtra(Constant.DATA_1) as ICPageOverview
        } catch (e: Exception) {
            null
        }

        if (pageOverview != null) {
            pageId = pageOverview!!.id!!
            onInfoData.postValue(pageOverview)
            getListUserFollowPage()
        } else {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
        }
    }

    fun getListUserFollowPage(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            offset = 0
        }

        interactor.getUserFollowPage(pageId, APIConstants.LIMIT, offset, object : ICNewApiListener<ICResponse<ICListResponse<ICSearchUser>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICSearchUser>>) {
                offset += APIConstants.LIMIT

                if (!isLoadMore) {
                    onCountUserData.postValue(obj.data!!.count)
                }

                val list = mutableListOf<Any>()
                for (item in obj.data!!.rows) {
                    list.add(item)
                }
                onListUserData.postValue(list)
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)))
            }
        })
    }
}
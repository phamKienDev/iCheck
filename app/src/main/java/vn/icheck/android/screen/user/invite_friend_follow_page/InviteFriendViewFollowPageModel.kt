package vn.icheck.android.screen.user.invite_friend_follow_page

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICFriendNofollowPage
import vn.icheck.android.network.models.ICUser

class InviteFriendViewFollowPageModel : ViewModel() {
    val interactor = PageRepository()
    val onSetData = MutableLiveData<ICFriendNofollowPage>()
    val onAddData = MutableLiveData<MutableList<ICUser>>()
    val onInvitationSuccess = MutableLiveData<Int>()
    val onError = MutableLiveData<ICError>()
    val onState = MutableLiveData<ICMessageEvent.Type>()

    var pageId: Long = -1
    var offset = 0

    fun getData(intent: Intent) {
        pageId = try {
            intent.getLongExtra(Constant.DATA_1, -1)
        } catch (e: Exception) {
            -1
        }
        onState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)
        if (pageId != -1L) {
            getFriendNofollowPage(null)
        } else {
            onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
        }
    }

    fun getFriendNofollowPage(filterString: String?, isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onError.postValue(ICError(R.drawable.ic_error_network,
                    ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }

        if (!isLoadmore) {
            offset = 0
        }

        interactor.getFriendNofollowPage(pageId, filterString, offset, object : ICNewApiListener<ICResponse<ICFriendNofollowPage>> {
            override fun onSuccess(obj: ICResponse<ICFriendNofollowPage>) {
                onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                offset += APIConstants.LIMIT
                if (isLoadmore) {
                    onAddData.postValue(obj.data?.rows!!)
                } else {
                    onSetData.postValue(obj.data)
                }
            }

            override fun onError(error: ICResponseCode?) {
                onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onError.postValue(ICError(R.drawable.ic_error_request,
                        ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    fun inivitUserFollowPage(list: MutableList<ICUser>) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance().applicationContext)) {
            onError.postValue(ICError(R.drawable.ic_error_network,
                    ICheckApplication.getInstance().applicationContext.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, null))
            return
        }
        val listId = mutableListOf<Long>()
        for (i in list) {
            listId.add(i.id)
        }

        if (!listId.isNullOrEmpty()) {
            onState.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

            interactor.postFollowPageInvitation(pageId, listId, object : ICNewApiListener<ICResponse<Boolean>> {
                override fun onSuccess(obj: ICResponse<Boolean>) {
                    onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onInvitationSuccess.postValue(listId.size)
                }

                override fun onError(error: ICResponseCode?) {
                    onState.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                    onError.postValue(ICError(R.drawable.ic_error_request,
                            ICheckApplication.getInstance().applicationContext.getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
                }
            })
        }

    }
}
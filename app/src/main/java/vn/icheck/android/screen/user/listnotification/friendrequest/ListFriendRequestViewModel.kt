package vn.icheck.android.screen.user.listnotification.friendrequest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICSearchUser

class ListFriendRequestViewModel : ViewModel() {
    private val interaction = RelationshipInteractor()

    val onSetMessage = MutableLiveData<ICError>()
    val onSetData = MutableLiveData<MutableList<ICSearchUser>>()
    val onAddData = MutableLiveData<MutableList<ICSearchUser>>()

    var offset = 0

    fun getFriendRequest(filter: String, isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onSetMessage.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        interaction.dispose()

        if (!isLoadMore) {
            offset = 0
        }

        interaction.getListFriendRequest(filter, null, offset, APIConstants.LIMIT, object : ICNewApiListener<ICResponse<ICListResponse<ICSearchUser>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICSearchUser>>) {
                offset += APIConstants.LIMIT

                for (item in obj.data?.rows ?: mutableListOf()) {
                    item.requestStatus = Constant.FRIEND_REQUEST_AWAIT
                }

                if (isLoadMore) {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                } else {
                    if (obj.data?.rows.isNullOrEmpty()) {
                        if (filter.isEmpty()) {
                            onSetMessage.postValue(ICError(R.drawable.ic_no_campaign, ICheckApplication.getString(R.string.khong_co_loi_moi_ket_ban_nao), null, 0))
                        } else {
                            onSetMessage.postValue(ICError(R.drawable.ic_search_90dp, ICheckApplication.getString(R.string.message_not_found), null, 0))
                        }
                    } else {
                        onSetData.postValue(obj.data?.rows ?: mutableListOf())
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                onSetMessage.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)))
            }
        })
    }

    fun dispose() {
        interaction.dispose()
    }
}
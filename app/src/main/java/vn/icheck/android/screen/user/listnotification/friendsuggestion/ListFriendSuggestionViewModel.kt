package vn.icheck.android.screen.user.listnotification.friendsuggestion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.ICFriendSuggestion
import vn.icheck.android.network.models.ICUser

class ListFriendSuggestionViewModel : ViewModel() {
    private val interaction = RelationshipInteractor()

    val onSetData = MutableLiveData<MutableList<ICUser>>()
    val onAddData = MutableLiveData<MutableList<ICUser>>()
    val onError = MutableLiveData<ICError>()

    var offset = 0
    var listFriendData:ICResponse<ICListResponse<ICUser>>? = null

    fun getFriendRequest(isLoadMore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        if (!isLoadMore) {
            interaction.dispose()
            offset = 0
        }

        interaction.getListFriendSuggestion(offset, 30, object : ICNewApiListener<ICResponse<ICListResponse<ICUser>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICUser>>) {
                offset += APIConstants.LIMIT
                listFriendData = obj
                if (isLoadMore) {
                    onAddData.postValue(obj.data?.rows ?: mutableListOf())
                } else {
                    onSetData.postValue(obj.data?.rows ?: mutableListOf())
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)))
            }
        })
    }

    fun getFriendRequest(isLoadMore: Boolean = false, filterString: String) {
       val filter = listFriendData?.data?.rows?.filter {
           it.getName.contains(filterString, true)
       }?.toMutableList()
        onSetData.postValue(filter?: mutableListOf())
    }
}
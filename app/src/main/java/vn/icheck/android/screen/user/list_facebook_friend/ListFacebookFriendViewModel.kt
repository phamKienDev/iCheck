package vn.icheck.android.screen.user.list_facebook_friend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.suggest.SuggestInteractor
import vn.icheck.android.network.models.ICUser


class ListFacebookFriendViewModel : ViewModel() {

    val onSetData = MutableLiveData<ICListResponse<ICUser>>()
    val onAddData = MutableLiveData<MutableList<ICUser>>()
    val onError = MutableLiveData<ICError>()

    val interactor = SuggestInteractor()
    var offset = 0

    fun getListFriend(filter: String, isLoadmore: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }
        if (!isLoadmore)
            offset = 0

        SessionManager.session.user?.let {
            interactor.getSuggestFriend(it.id, offset, APIConstants.LIMIT, filter, object : ICNewApiListener<ICResponse<ICListResponse<ICUser>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICUser>>) {
                    offset += APIConstants.LIMIT
                    if (isLoadmore) {
                        onAddData.postValue(obj.data!!.rows)
                    } else {
                        onSetData.postValue(obj.data!!)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    onError.postValue(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai)))
                }
            })
        }
    }
}
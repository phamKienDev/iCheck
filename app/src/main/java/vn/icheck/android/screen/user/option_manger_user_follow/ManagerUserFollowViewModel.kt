package vn.icheck.android.screen.user.option_manger_user_follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.wall.ICUserFollowWall

class ManagerUserFollowViewModel : ViewModel() {

    private val interactor = UserInteractor()

    private var page = 0

    val listData = MutableLiveData<ICListResponse<ICUserFollowWall>>()
    val addFriend = MutableLiveData<Boolean>()
    val isLoadMoreData = MutableLiveData<Boolean>()
    val errorData = MutableLiveData<Int>()
    val errorPutData = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    fun getListUserFollow(key: String? = null, isLoadMore: Boolean = false, isSearch: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        if (!isLoadMore) page = 0

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.getListUserFollow(key, page, object : ICNewApiListener<ICResponse<ICListResponse<ICUserFollowWall>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICUserFollowWall>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data?.rows != null && !obj.data?.rows.isNullOrEmpty()) {
                    page += APIConstants.LIMIT
                    if (!isLoadMore) {
                        isLoadMoreData.postValue(true)
                    }
                    listData.postValue(obj.data)
                } else {
                    if (!isLoadMore) {
                        if (!key.isNullOrEmpty()) {
                            errorData.postValue(Constant.ERROR_EMPTY_SEARCH)
                        } else {
                            errorData.postValue(Constant.ERROR_EMPTY_FOLLOW)
                        }
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorData.postValue(Constant.ERROR_SERVER)
            }
        })
    }

    fun addFriend(id: Long?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorPutData.postValue(Constant.ERROR_INTERNET)
            return
        }

        if (id == null) {
            errorPutData.postValue(Constant.ERROR_UNKNOW)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.putAddFriend(id, null, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.statusCode == "200") {
                    addFriend.postValue(true)
                } else {
                    errorPutData.postValue(Constant.ERROR_SERVER)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorPutData.postValue(Constant.ERROR_SERVER)
            }
        })
    }
}
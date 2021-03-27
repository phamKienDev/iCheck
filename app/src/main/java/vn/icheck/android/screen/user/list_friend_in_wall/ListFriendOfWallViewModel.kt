package vn.icheck.android.screen.user.list_friend_in_wall

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.network.models.wall.ICUserFollowWall

class ListFriendOfWallViewModel : ViewModel() {

    private val interactor = UserInteractor()

    private var page = 0
    var friendCount = 0

    val errorData = MutableLiveData<Int>()
    val errorDataPut = MutableLiveData<Int>()
    val statusCode = MutableLiveData<ICMessageEvent.Type>()

    val listData = MutableLiveData<MutableList<ICUserFollowWall>>()
    val isLoadMoreData = MutableLiveData<Boolean>()
    val unFriend = MutableLiveData<Boolean>()
    val followOrUnFollow = MutableLiveData<Boolean>()
    val listReport = MutableLiveData<MutableList<ICReportForm>>()
    val reportSuccess = MutableLiveData<MutableList<ICReportForm>>()
    val invitationFriend = MutableLiveData<Int>()

    fun getListFriend(key: String? = null, isLoadMore: Boolean = false, uid: Long? = null, isSearch: Boolean = false) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            statusCode.postValue(ICMessageEvent.Type.ON_NO_INTERNET)
            return
        }

        val id: Long = uid ?: SessionManager.session.user?.id ?: 0L
        if (id == 0L) return

        if (!isLoadMore) page = 0

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.getListFriendOfUser(id, key, page, object : ICNewApiListener<ICResponse<ICListResponse<ICUserFollowWall>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICUserFollowWall>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.data?.rows != null && !obj.data?.rows.isNullOrEmpty()) {
                    page += APIConstants.LIMIT
                    if (!isLoadMore) {
                        isLoadMoreData.postValue(true)
                    }
                    friendCount = obj.data?.count ?: 0
                    listData.postValue(obj.data?.rows)
                } else {
                    if (!isLoadMore) {
                        if (isSearch) {
                            errorData.postValue(Constant.ERROR_EMPTY_SEARCH)
                        } else {
                            errorData.postValue(Constant.ERROR_EMPTY)
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

    fun unFriend(id: Long?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorDataPut.postValue(Constant.ERROR_INTERNET)
            return
        }

        if (id == null) {
            errorDataPut.postValue(Constant.ERROR_UNKNOW)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.putUnFriend(id, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.statusCode == "200") {
                    unFriend.postValue(true)
                } else {
                    errorDataPut.postValue(Constant.ERROR_SERVER)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorDataPut.postValue(Constant.ERROR_SERVER)
            }
        })
    }

    fun unFollowFriend(id: Long?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorDataPut.postValue(Constant.ERROR_INTERNET)
            return
        }

        if (id == null) {
            errorDataPut.postValue(Constant.ERROR_UNKNOW)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.followUser(id, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.statusCode == "200") {
                    followOrUnFollow.postValue(true)
                } else {
                    errorDataPut.postValue(Constant.ERROR_SERVER)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorDataPut.postValue(Constant.ERROR_SERVER)
            }
        })
    }

    fun getWrongReport() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorDataPut.postValue(Constant.ERROR_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.getListReportUser(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    listReport.postValue(obj.data?.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorDataPut.postValue(Constant.ERROR_SERVER)
            }
        })
    }

    fun sendReportuser(idUser: Long?, listReason: MutableList<Int>, listMessage: MutableList<String>, message: String) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorDataPut.postValue(Constant.ERROR_INTERNET)
            return
        }

        if (idUser == null) {
            errorDataPut.postValue(Constant.ERROR_UNKNOW)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.sendReportUser(idUser, listReason, message, object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)

                val list = mutableListOf<ICReportForm>()
                for (item in listMessage) {
                    list.add(ICReportForm(null, item))
                }
                if (message.isNotEmpty()) {
                    list.add(ICReportForm(null, message))
                }

                reportSuccess.postValue(list)
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorDataPut.postValue(Constant.ERROR_SERVER)
            }
        })
    }

    fun friendInvitation(id: Long, status: Int?) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            errorDataPut.postValue(Constant.ERROR_INTERNET)
            return
        }

        statusCode.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interactor.putAddFriend(id, status, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                if (obj.statusCode == "200") {
                    invitationFriend.postValue(status ?: 1)
                } else {
                    errorDataPut.postValue(Constant.ERROR_SERVER)
                }
            }

            override fun onError(error: ICResponseCode?) {
                statusCode.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                errorDataPut.postValue(Constant.ERROR_SERVER)
            }
        })
    }
}
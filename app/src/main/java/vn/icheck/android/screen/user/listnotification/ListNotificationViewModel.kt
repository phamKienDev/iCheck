package vn.icheck.android.screen.user.listnotification

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.api.ICKApi
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.notification.NotificationInteractor
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.relationship.RelationshipInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.user.home_page.model.ICHomeItem
import vn.icheck.android.util.ick.logError

class ListNotificationViewModel @ViewModelInject constructor(val ickApi: ICKApi, @Assisted savedStateHandle: SavedStateHandle) : ViewModel() {
    private val interaction = NotificationInteractor()
    private val relationshipInteraction = RelationshipInteractor()
    private val pageInteraction = PageRepository()

    val onAddData = MutableLiveData<ICHomeItem>()
    val onUpdateData = MutableLiveData<ICHomeItem>()
    val onError = MutableLiveData<ICError>()

    val onStatus = MutableLiveData<ICMessageEvent.Type>()
    val onMarkAllSuccess = MutableLiveData<String>()
    val onShowErrorMessage = MutableLiveData<String>()

    private var totalRequest = 0
    private var isRequestSuccess = false

    var updateNotify = 0
    var totalNotify = 0

    // Limit offset for load more posts
    var currentLimitPost = 10
    var currentOffsetPost = 20

    val arrNotify = arrayListOf<ICNotification>()

    fun getLayout() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onError.postValue(ICError(R.drawable.ic_error_network, ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)))
            return
        }

        interaction.dispose()
        relationshipInteraction.dispose()
        updateNotify = 0
        arrNotify.clear()
        currentOffsetPost = 20
        interaction.getLayoutNotification(object : ICNewApiListener<ICLayoutData<ICListResponse<ICNotification>>> {
            override fun onSuccess(obj: ICLayoutData<ICListResponse<ICNotification>>) {
                totalRequest = 0
                isRequestSuccess = false

                if (!obj.layout.isNullOrEmpty()) {
                    for (i in 0 until (obj.layout?.size!!)) {
                        val item = obj.layout!![i]

                        when (item.id.toString()) {
                            "product-notification-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = ICHomeItem(ICViewTypes.PRODUCT_NOTICE_TYPE, item.id.toString())
                                    getProductNotice(item)
                                }
                            }
                            "friend-invitation-notification-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = ICHomeItem(ICViewTypes.FRIEND_INVITATION_TYPE, item.id.toString())
                                    getFriendRequest(item)
                                }
                            }
                            "friend-suggestion-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = ICHomeItem(ICViewTypes.FRIEND_SUGGESTION_TYPE,  item.id.toString())
                                    getFriendSuggestion(item)
                                }
                            }
                            "page-suggestion-1" -> { // - Error
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = ICHomeItem(ICViewTypes.RELATED_PAGE_TYPE, item.id.toString())
                                    getNotificationPage(item)
                                }
                            }
                            "reaction-notification-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = ICHomeItem(ICViewTypes.INTERACTIVE_TYPE,  item.id.toString(), item.custom)
                                    getReactionNotification(item)
                                }
                            }
                            "other-notification-1" -> {
                                if (!item.request.url.isNullOrEmpty()) {
                                    totalRequest++
                                    onAddData.value = ICHomeItem(ICViewTypes.OTHER_NOTIFICATION_TYPE,  item.id.toString())
                                    getOtherNotification(item)
                                }
                            }
                        }
                    }
                }

                if (totalRequest == 0) {
                    onError.postValue(ICError(R.drawable.ic_no_campaign, ICheckApplication.getInstance().getString(R.string.ban_chua_co_thong_bao_nao), null, 0))
                }
            }

            override fun onError(error: ICResponseCode?) {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
            }
        })
    }

    @Synchronized
    private fun finishRequest(isSuccess: Boolean) {
        if (isSuccess) {
            isRequestSuccess = true
        }

        totalRequest--

        if (totalRequest <= 0) {
            if (isRequestSuccess) {
                onError.postValue(ICError(R.drawable.ic_no_campaign, ICheckApplication.getInstance().getString(R.string.ban_chua_co_thong_bao_nao), null, 0))
            } else {
                onError.postValue(ICError(R.drawable.ic_error_request, ICheckApplication.getInstance().getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)))
            }
        }
    }

    private fun getReactionNotification(widget: ICLayout) {
        interaction.getListNotification(widget.request.url!!, object : ICNewApiListener<ICLayoutData<ICListResponse<ICNotification>>> {
            override fun onSuccess(obj: ICLayoutData<ICListResponse<ICNotification>>) {
                // Cập nhật data cho item null trong listData ở adapter
                if (!obj.data?.rows.isNullOrEmpty()) {
                    val icHome = ICHomeItem(ICViewTypes.INTERACTIVE_TYPE, widget.id.toString())
                    icHome.data = obj.data?.rows
                    onUpdateData.value = icHome
                } else {
                    finishRequest(true)
                }
            }

            override fun onError(error: ICResponseCode?) {
                // Truyền data = null để xóa item null trong listData ở adapter
                val icHome = ICHomeItem()
                icHome.widgetID = widget.id.toString()
                onUpdateData.value = icHome

                finishRequest(false)
            }
        })
    }

    private fun getNotificationPage(widget: ICLayout) {
        pageInteraction.getNotificationPage(widget.request.type, widget.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICNotificationPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICNotificationPage>>) {
                // Cập nhật data cho item null trong listData ở adapter
                if (!obj.data?.rows.isNullOrEmpty()) {
                    val icHome = ICHomeItem(ICViewTypes.RELATED_PAGE_TYPE, widget.id.toString())
                    icHome.data = obj.data?.rows
                    onUpdateData.value = icHome
                } else {
                    finishRequest(true)
                }
            }

            override fun onError(error: ICResponseCode?) {
                // Truyền data = null để xóa item null trong listData ở adapter
                onUpdateData.value = ICHomeItem().apply {
                    widgetID = widget.id.toString()
                }

                finishRequest(false)
            }
        })
    }

    private fun getFriendSuggestion(widget: ICLayout) {
        relationshipInteraction.getListFriendSuggestion(widget.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICUser>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICUser>>) {
                // Cập nhật data cho item null trong listData ở adapter
                if (!obj.data?.rows.isNullOrEmpty()) {
                    val icHome = ICHomeItem(ICViewTypes.FRIEND_SUGGESTION_TYPE, widget.id.toString())
                    icHome.data = obj.data!!.rows
                    onUpdateData.value = icHome
                } else {
                    finishRequest(true)
                }
            }

            override fun onError(error: ICResponseCode?) {
                // Truyền data = null để xóa item null trong listData ở adapter
                onUpdateData.value = ICHomeItem().apply {
                    widgetID = widget.id.toString()
                }

                finishRequest(false)
            }
        })
    }

    private fun getFriendRequest(widget: ICLayout) {
        relationshipInteraction.getListFriendRequest(widget.request.url!!, object : ICNewApiListener<ICResponse<ICListResponse<ICSearchUser>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICSearchUser>>) {
                // Cập nhật data cho item null trong listData ở adapter
                if (obj.data != null && obj.data!!.count > 0) {
                    val icHome = ICHomeItem(ICViewTypes.FRIEND_INVITATION_TYPE, widget.id.toString())
                    icHome.data = obj.data
                    onUpdateData.value = icHome
                } else {
                    finishRequest(true)
                }
            }

            override fun onError(error: ICResponseCode?) {
                // Truyền data = null để xóa item null trong listData ở adapter
                onUpdateData.value = ICHomeItem().apply {
                    widgetID = widget.id.toString()
                }

                finishRequest(false)
            }
        })
    }

    private fun getProductNotice(widget: ICLayout) {
        interaction.getListNotification(widget.request.url!!, object : ICNewApiListener<ICLayoutData<ICListResponse<ICNotification>>> {
            override fun onSuccess(obj: ICLayoutData<ICListResponse<ICNotification>>) {
                // Cập nhật data cho item null trong listData ở adapter
                if (!obj.data?.rows.isNullOrEmpty()) {
                    val icHome = ICHomeItem(ICViewTypes.PRODUCT_NOTICE_TYPE, widget.id.toString())
                    icHome.data = obj.data?.rows
                    onUpdateData.value = icHome
                } else {
                    finishRequest(true)
                }
            }

            override fun onError(error: ICResponseCode?) {
                // Truyền data = null để xóa item null trong listData ở adapter
                val icHome = ICHomeItem()
                icHome.widgetID = widget.id.toString()
                onUpdateData.value = icHome

                finishRequest(false)
            }
        })
    }

    private fun getOtherNotification(widget: ICLayout) {
        interaction.getListNotification(widget.request.url!!, object : ICNewApiListener<ICLayoutData<ICListResponse<ICNotification>>> {
            override fun onSuccess(obj: ICLayoutData<ICListResponse<ICNotification>>) {
                totalNotify = obj.data?.count ?: 0

                // Cập nhật data cho item null trong listData ở adapter
                if (!obj.data?.rows.isNullOrEmpty()) {
                    obj.data?.rows?.forEach {
                        val icHome = ICHomeItem(ICViewTypes.OTHER_NOTIFICATION_TYPE, widget.id.toString())
                        if (arrNotify.isEmpty()) {
                            it.showTitle = true
                        }
                        icHome.data = it
                        onAddData.value = icHome
                    }
                } else {
                    finishRequest(true)
                }
            }

            override fun onError(error: ICResponseCode?) {
                // Truyền data = null để xóa item null trong listData ở adapter
                val icHome = ICHomeItem()
                icHome.widgetID = widget.id.toString()
                onUpdateData.value = icHome

                finishRequest(false)
            }
        })
    }

    fun getPosts() {
        updateNotify = 1
        viewModelScope.launch {
            try {
                val obj = ickApi.getNotification(offset = currentOffsetPost)
                obj.data?.rows?.forEach {
                    val icHome = ICHomeItem(ICViewTypes.OTHER_NOTIFICATION_TYPE, "other-notification-1")
                    icHome.data = it
                    onAddData.value = icHome
                }
                updateNotify = if (obj.data?.rows.isNullOrEmpty()) {
                    1
                } else {
                    0
                }
            } catch (e: Exception) {
                logError(e)
            }
        }
    }

    fun markReadAll() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            onShowErrorMessage.postValue(ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        onStatus.postValue(ICMessageEvent.Type.ON_SHOW_LOADING)

        interaction.markReadAllNotification(object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onMarkAllSuccess.postValue(ICheckApplication.getString(R.string.ban_da_doc_tat_ca_cac_thong_bao))
            }

            override fun onError(error: ICResponseCode?) {
                onStatus.postValue(ICMessageEvent.Type.ON_CLOSE_LOADING)
                onShowErrorMessage.postValue(ICheckApplication.getError(error?.message))
            }
        })
    }

    fun disposeApi() {
        interaction.dispose()
        relationshipInteraction.dispose()
    }
}
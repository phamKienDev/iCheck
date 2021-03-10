package vn.icheck.android.network.feature.notification

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICLayoutData
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.network.models.ICReqMarkRead
import java.util.*

class NotificationInteractor : BaseInteractor() {

//    fun getListNotification(offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICNotification>>) {
//        val params = HashMap<String, Any>()
//        params["offset"] = offset
//        params["limit"] = limit
//
//        requestApi(ICNetworkClient.getApiClient().getListNotification(params), listener)
//    }

    fun getLayoutNotification(listener: ICNewApiListener<ICLayoutData<ICListResponse<ICNotification>>>) {
        val url = APIConstants.socialHost + APIConstants.Layout.LAYOUTS

        val params = HashMap<String, Any>()
        params["layout"] = "notification-page"
//        params["notificationType"] = 1

        requestNewApi(ICNetworkClient.getSocialApi().getListNotification(url, params), listener)
    }

    fun getListNotification(layout: String?, type: Int?, offset: Int, limit: Int, listener: ICNewApiListener<ICLayoutData<ICListResponse<ICNotification>>>) {
        val url = APIConstants.socialHost + APIConstants.Social.LIST_NOTIFICATIONS

        val params = HashMap<String, Any>()
        if (!layout.isNullOrEmpty())
            params["layout"] = layout

        if (type != null)
            params["notificationType"] = type

        params["offset"] = offset
        params["limit"] = limit

        requestNewApi(ICNetworkClient.getSocialApi().getListNotification(url, params), listener)
    }

    fun getListNotification(path: String, listener: ICNewApiListener<ICLayoutData<ICListResponse<ICNotification>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getListNotification(url), listener)
    }

    fun markReadNotification(body: ICReqMarkRead, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val request = hashMapOf<String, Any?>()
        body.id?.let {
            request["idList"] = listOf(it)
        }
        body.idList?.let {
            request["idList"] = it
        }
        requestNewApi(ICNetworkClient.getSocialApi().markReadNotification(request), listener)
    }

    fun markReadAllNotification(listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any?>()
        body["isAll"] = true
        requestNewApi(ICNetworkClient.getSocialApi().markReadNotification(body), listener)
    }

    fun unsubscribeNotification(id: Long, type: String?, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val url = APIConstants.socialHost + APIConstants.Notify.UNSUBCRIBE
        val body = hashMapOf<String, Any>()
        body["entityId"] = id
        if (!type.isNullOrEmpty())
            body["entityType"] = type.toUpperCase(Locale.ROOT)
//        entityType: = 1 = product,
//        entityType = 2 post

        requestNewApi(ICNetworkClient.getSocialApi().subcribeNotification(url, body), listener)
    }

    fun deleteNotification(id: Long, listener: ICNewApiListener<ICResponse<Any>>) {
        requestNewApi(ICNetworkClient.getSocialApi().deleteNotification(id, hashMapOf()), listener)
    }
}
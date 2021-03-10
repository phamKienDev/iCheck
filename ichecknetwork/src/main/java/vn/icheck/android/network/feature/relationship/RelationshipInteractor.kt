package vn.icheck.android.network.feature.relationship

import vn.icheck.android.network.base.*
import vn.icheck.android.network.base.APIConstants.socialHost
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*

class RelationshipInteractor : BaseInteractor() {

    fun getListFriendSuggestion(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICUser>>>) {
        val url = socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getSocialApi().getListFriendSuggestion(url, hashMapOf()), listener)
    }

    fun getListFriendSuggestion(offset: Int, limit: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICUser>>>, filter: String? = null) {
        val url = socialHost + APIConstants.Social.LIST_FRIEND_SUGGESTION

        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = limit
        if (!filter.isNullOrEmpty()) {
            params["filterString"] = filter
        }
        requestNewApi(ICNetworkClient.getSocialApi().getListFriendSuggestion(url, params), listener)
    }

    fun inviteFriend(id: Long, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val url = socialHost + APIConstants.Social.UPDATE_FRIEND_INVITATION

        val body = hashMapOf<String, Any>()
        body["userId"] = id

        requestNewApi(ICNetworkClient.getSocialApi().updateFriendInvitation(url, body), listener)
    }

    fun inviteFriend(id: Long, status: Int?, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val url = socialHost + APIConstants.Social.UPDATE_FRIEND_INVITATION

        val body = hashMapOf<String, Any>()
        body["userId"] = id
        if (status != null) {
            body["status"] = status
        }

        requestNewApi(ICNetworkClient.getSocialApi().updateFriendInvitation(url, body), listener)
    }

    fun removeFriendSuggestion(id: Long, listener: ICNewApiListener<ICResponse<List<ICUser>>>) {
        val body = hashMapOf<String, Any?>()
        body["userIdList"] = arrayListOf<Long>(id)

        requestNewApi(ICNetworkClient.getSocialApi().removeFriendSuggestion(body), listener)
    }

    fun getListFriendRequest(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICSearchUser>>>) {
        val url =  socialHost + "social/api" + path
        requestNewApi(ICNetworkClient.getSocialApi().getListFriendRequest(url, hashMapOf()), listener)
    }

    fun getListFriendRequest(filter: String, status: Int?, offset: Int, limit: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICSearchUser>>>) {
        val url = socialHost + APIConstants.Social.LIST_FRIEND_INVITATION

        val params = hashMapOf<String, Any>()
        if (filter.isNotEmpty()) {
            params["filterString"] = filter
        }
        if (status != null) {
            params["status"] = status
        }
        params["offset"] = offset
        params["limit"] = limit

        requestNewApi(ICNetworkClient.getSocialApi().getListFriendRequest(url, params), listener)
    }

    fun updateFriendInvitation(id: Long, status: Int, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val url = socialHost + APIConstants.Social.UPDATE_FRIEND_INVITATION

        val body = hashMapOf<String, Any>()
        body["userId"] = id
        body["status"] = status

        requestNewApi(ICNetworkClient.getSocialApi().updateFriendInvitation(url, body), listener)
    }

    fun followPage(pageIdList: List<Long>, listener: ICNewApiListener<ICResponse<Boolean>>) {
        if (!pageIdList.isNullOrEmpty()) {
            val body = hashMapOf<String, Any>()
            body["pageIdList"] = pageIdList

            requestNewApi(ICNetworkClient.getSocialApi().relationshipFollowPage(body), listener)
        }
    }

    fun unFollowPage(id: Long, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        body["pageId"] = id

        requestNewApi(ICNetworkClient.getSocialApi().unFollowPage(body), listener)
    }

    fun meFollowUser(listener: ICNewApiListener<ICResponse<ICListResponse<ICNotification>>>, status: Int = 1) {
        val body = hashMapOf<String, Any>()
        body["status"] = status

        requestNewApi(ICNetworkClient.getSocialApi().getMeFollowUser(body), listener)
    }

    fun followUser(id: Long, status: Int, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        body["targetUserId"] = id
        body["status"] = status

        requestNewApi(ICNetworkClient.getSocialApi().followUser(body), listener)
    }

    fun postNotification(id: Long, type: String, disableNotify: Boolean, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        body["entityId"] = id
        body["entityType"] = type

        val url = if (disableNotify) {
            socialHost + APIConstants.PATH + APIConstants.Review.RE_NOTICATION
        } else {
            socialHost + APIConstants.PATH + APIConstants.Review.UN_NOTICATION
        }

        requestNewApi(ICNetworkClient.getSocialApi().postNotification(url, body), listener)
    }
}
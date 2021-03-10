package vn.icheck.android.network.feature.suggest

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICSuggestPage
import vn.icheck.android.network.models.ICSuggestTopic
import vn.icheck.android.network.models.ICUser

class SuggestInteractor : BaseInteractor() {

    fun getListTopic(offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICSuggestTopic>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = 10
//        params["option"] = "suggestion"
        requestNewApi(ICNetworkClient.getSocialApi().getListTopic(params), listener)
    }

    fun getSuggestPage(offset: Int, listCaterogies: MutableList<Int>?, listner: ICNewApiListener<ICResponse<ICListResponse<ICSuggestPage>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = 10
        if (!listCaterogies.isNullOrEmpty()) {
            params["categoryIds"] = listCaterogies
        }
        requestNewApi(ICNetworkClient.getSocialApi().getSuggestPage(params), listner)
    }

    fun getSuggestFriend(userId: Long, offset: Int, limit: Int, filter: String?, listner: ICNewApiListener<ICResponse<ICListResponse<ICUser>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = limit
        if (filter != null && filter != "") {
            params["filterString"] = filter
        }
        requestNewApi(ICNetworkClient.getSocialApi().getSuggestFriend(userId, params), listner)
    }


    suspend fun postFavouriteTopic(favouriteTopicIdList: MutableList<Int>?): ICResponse<Boolean> {
        return if (!favouriteTopicIdList.isNullOrEmpty()) {
            val params = hashMapOf<String, Any>()
            params["favouriteTopicIdList"] = favouriteTopicIdList
            ICNetworkClient.getSocialApi().postFavouriteTopic(params)
        } else {
            ICResponse(false)
        }
    }

    suspend fun postFollowPage(listPage: MutableList<Int>?): ICResponse<Boolean> {
        return if (!listPage.isNullOrEmpty()) {
            val params = hashMapOf<String, Any>()
            params["pageIdList"] = listPage
            ICNetworkClient.getSocialApi().postFollowPage(params)
        } else {
            ICResponse(false)
        }
    }
}
package vn.icheck.android.network.feature.page

import com.google.gson.JsonObject
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.product.report.ICReportForm

class PageRepository : BaseInteractor() {

    fun getLayoutPage(id: Long, pageType: String, listener: ICNewApiListener<ICLayoutData<JsonObject>>) {
        val url = APIConstants.socialHost + APIConstants.Layout.PAGES.replace("{id}", id.toString())

        val params = hashMapOf<String, Any>()
        params["layout"] = "page-detail"
        params["objectType"] = pageType

        requestNewApi(ICNetworkClient.getNewSocialApi().getLayoutPage(url, params), listener)
    }

    fun getBrands(id: Long, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPageTrend>>>) {
        val url = APIConstants.socialHost + APIConstants.Page.GET_BRAND.replace("{id}", id.toString())

        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        requestNewApi(ICNetworkClient.getSocialApi().getBrandPage(url, params), listener)
    }

    fun getBrands(path: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPageTrend>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getBrandPage(url), listener)
    }

    fun getHighlightProducts(id: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = 0
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getSocialApi().getHighlightProducts(id, params), listener)
    }

    fun getHighlightProducts(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICProductTrend>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getHighlightProducts(url), listener)
    }

    fun getCategoriesProduct(id: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICCategoriesProduct>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getCategoriesProduct(id), listener)
    }

    fun getCategoriesProduct(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICCategoriesProduct>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getCategoriesProduct(url), listener)
    }

    fun getRelatedPage(path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICRelatedPage>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getRelatedPage(url), listener)
    }

    fun followPage(pageID: Long, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val url = APIConstants.socialHost + APIConstants.Social.FOLLOW_PAGE
        val body = hashMapOf<String, Any>()
        body["pageIdList"] = listOf(pageID)
        requestNewApi(ICNetworkClient.getNewSocialApi().followPage(url, body), listener)
    }

    fun unFollowPage(pageID: Long, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val url = APIConstants.socialHost + APIConstants.Social.UN_FOLLOW_PAGE
        val body = hashMapOf<String, Any>()
        body["pageId"] = pageID
        requestNewApi(ICNetworkClient.getNewSocialApi().unFollowPage(url, body), listener)
    }

    fun getImageAssetsPage(page: Int, pageID: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICMediaPage>>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = page
        params["limit"] = APIConstants.LIMIT
        requestNewApi(ICNetworkClient.getSocialApi().getImageAssetsPage(pageID, params), listener)
    }

    fun getImageAssetsPage(path: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICMediaPage>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path
        requestNewApi(ICNetworkClient.getNewSocialApi().getImageAssetsPage(url), listener)
    }

    fun getListReportFormPage(listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getListReportPage(), listener)
    }

    fun sendReportPage(idPage: Long, reportElementIdList: MutableList<Int>, description: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        val body = hashMapOf<String, Any>()
        body["pageId"] = idPage
        if (!reportElementIdList.isNullOrEmpty()) {
            body["reportElementIdList"] = reportElementIdList
        }
        if (!description.isNullOrEmpty()) {
            body["description"] = description
        }
        requestNewApi(ICNetworkClient.getSocialApi().postReportPage(body), listener)
    }

    fun getNotificationPage(method: String?, path: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICNotificationPage>>>) {
        val url = APIConstants.socialHost + APIConstants.PATH + path

        if (method == APIConstants.POST) {
            requestNewApi(ICNetworkClient.getSocialApi().getNotificationPage(url, JsonObject()), listener)
        } else {
            requestNewApi(ICNetworkClient.getSocialApi().getNotificationPage(url), listener)
        }
    }

    fun getMapPageHistory(listener: ICNewApiListener<ICResponse<ICPageDetail>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getMapPageHistory(), listener)
    }

    fun getListPostOfPage(pageID: Long, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPost>>>) {
        val body = hashMapOf<String, Int>()
        body["offset"] = offset
        body["limit"] = APIConstants.LIMIT

        requestNewApi(ICNetworkClient.getSocialApi().getListPostsOfPage(pageID, body), listener)
    }

    fun getListPostOfPage(path: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPost>>>) {

        val url = APIConstants.socialHost + APIConstants.PATH + path
        val params = hashMapOf<String, Any>()
        params["limit"] = APIConstants.LIMIT
        params["offset"] = offset

        requestNewApi(ICNetworkClient.getSocialApi().getListPosts(url, params), listener)
    }

    fun getShareLinkOfPost(id: Long, listener: ICNewApiListener<ICResponse<String>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getShareLinkOfPost(id), listener)
    }

    fun postShareLinkOfPost(id: Long, listener: ICNewApiListener<ICResponse<String>>) {
        requestNewApi(ICNetworkClient.getSocialApi().postShareLinkOfPost(id, hashMapOf()), listener)
    }

    fun pinPostOfPage(id: Long, isPin: Boolean, pageID: Long?, listener: ICNewApiListener<ICResponse<ICPost>>) {
        val body = hashMapOf<String, Any>()
        body["postId"] = id
        body["pinned"] = isPin
        if (pageID != null)
            body["pageId"] = pageID

        requestNewApi(ICNetworkClient.getSocialApi().pinPostOfPage(body), listener)
    }

    fun subcribeNotification(id: Long, type: String, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val url = APIConstants.socialHost + APIConstants.Notify.RESUBCRIBE
        val body = hashMapOf<String, Any>()
        body["entityId"] = id
        body["entityType"] = type
        requestNewApi(ICNetworkClient.getSocialApi().subcribeNotification(url, body), listener)
    }

    fun unsubcribeNotification(id: Long, type: String, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val url = APIConstants.socialHost + APIConstants.Notify.UNSUBCRIBE
        val body = hashMapOf<String, Any>()
        body["entityId"] = id
        body["entityType"] = type
        requestNewApi(ICNetworkClient.getSocialApi().subcribeNotification(url, body), listener)
    }

    fun getListReportPost(listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getListReportPost(), listener)
    }

    fun reportPost(id: Long, listReason: MutableList<Int>, message: String, listener: ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>>) {
        val body = hashMapOf<String, Any>()
        body["postId"] = id
        if (listReason.isNotEmpty()) {
            body["reportElementIdList"] = listReason
        }
//        if (message.isNotEmpty()) {
        body["description"] = message
//        }

        requestNewApi(ICNetworkClient.getSocialApi().reportPost(body), listener)
    }


    fun getUserFollowPage(id: Long, limit: Int, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICSearchUser>>>) {
        val query = hashMapOf<String, Any>()
        query["limit"] = limit
        query["offset"] = offset
        requestNewApi(ICNetworkClient.getSocialApi().getListUserFollowPage(id, query), listener)
    }

    fun getRelationshipCurrentUser(listener: ICNewApiListener<ICResponse<ICRelationshipsInformation>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getRelationshipCurrentUser(), listener)
    }

    suspend fun getRelationshipInformation(): ICResponse<ICRelationshipsInformation> {
        return ICNetworkClient.getSocialApi().getRelationshipInformation()
    }

    fun getListDistributor(url: String, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPage>>>) {

        val urlRequest = APIConstants.PATH + url.replace("offset=0", "offset=$offset")

        requestNewApi(ICNetworkClient.getSocialApi().getListDistributor(urlRequest), listener)
    }

    fun getFriendNofollowPage(pageId: Long, filterString: String?, offset: Int, listener: ICNewApiListener<ICResponse<ICFriendNofollowPage>>) {
        val param = hashMapOf<String, Any>()
        if (filterString != null) {
            param["filterString"] = filterString
        }
        param["limit"] = APIConstants.LIMIT
        param["offset"] = offset
        requestNewApi(ICNetworkClient.getNewSocialApi().getFriendNofollowPage(pageId, param), listener)
    }

    fun postFollowPageInvitation(pageId: Long, userID: MutableList<Long>, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val param = hashMapOf<String, Any>()
        if (!userID.isNullOrEmpty()) {
            param["userIdList"] = userID
        }
        param["pageId"] = pageId
        requestNewApi(ICNetworkClient.getNewSocialApi().postFollowPageInvitation(param), listener)
    }

    fun unSubcribePage(id: Long?, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        if (id != null) {
            body["entityId"] = id
        }
        body["entityType"] = "page"
        requestNewApi(ICNetworkClient.getNewSocialApi().unSubcribePage(body), listener)
    }

    fun reSubcribePage(id: Long?, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val body = hashMapOf<String, Any>()
        if (id != null) {
            body["entityId"] = id
        }
        body["entityType"] = "page"
        requestNewApi(ICNetworkClient.getNewSocialApi().reSubcribePage(body), listener)
    }

    fun getPageUserManager(id: Long, listener: ICNewApiListener<ICResponse<ICListResponse<ICPageUserManager>>>) {
        requestNewApi(ICNetworkClient.getNewSocialApi().getPageUserManager(id), listener)
    }

    fun updatePage(pageId: Long, key: String, data: String, listener: ICNewApiListener<ICResponse<MutableList<Int>>>) {
        val body = hashMapOf<String, Any>()
        body[key] = data
        requestNewApi(ICNetworkClient.getNewSocialApi().updatePage(pageId, body), listener)
    }

    fun getMyOwnerPage(filterString: String?, limit: Int, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPage>>>) {
        val param = hashMapOf<String, Any>()
        if (filterString != null) {
            param["filterString"] = filterString
        }
        param["limit"] = limit
        param["offset"] = offset
        requestNewApi(ICNetworkClient.getNewSocialApi().getMyOwnerPage(param), listener)
    }

    suspend fun getMyOwnerPageV2(filterString: String?, limit: Int, offset: Int):ICResponse<ICListResponse<ICPage>> {
        val param = hashMapOf<String, Any>()
        if (filterString != null) {
            param["filterString"] = filterString
        }
        param["limit"] = limit
        param["offset"] = offset

        return ICNetworkClient.getNewSocialApi().getMyOwnerPageV2(param)
    }

    fun getMyFollowPage(filterString: String?, limit: Int, offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICPage>>>) {
        val param = hashMapOf<String, Any>()
        if (filterString != null) {
            param["filterString"] = filterString
        }
        param["limit"] = limit
        param["offset"] = offset
        requestNewApi(ICNetworkClient.getNewSocialApi().getMyFollowPage(param), listener)
    }

    suspend fun getMyFollowPageV2(filterString: String?, limit: Int, offset: Int): ICResponse<ICListResponse<ICPage>> {
        val param = hashMapOf<String, Any>()
        if (filterString != null) {
            param["filterString"] = filterString
        }
        param["limit"] = limit
        param["offset"] = offset

        return ICNetworkClient.getNewSocialApi().getMyFollowPageV2(param)
    }

    fun getButtonCustomizes(pageID: Long, listener: ICNewApiListener<ICResponse<MutableList<ICButtonOfPage>>>) {
        val param = hashMapOf<String, Any>()
        param["objectTypes"] = "extra_button,primary_button"
        requestNewApi(ICNetworkClient.getNewSocialApi().getButtonCustomize(pageID, param), listener)
    }

    fun updateButtonCustomizes(pageID: Long, list: MutableList<ICUpdateButtonPage>, listener: ICNewApiListener<ICResponse<MutableList<ICButtonOfPage>>>) {
        val param = hashMapOf<String, Any>()
        param["buttonConfigs"] = list
        requestNewApi(ICNetworkClient.getNewSocialApi().updateButtonCustomize(pageID, param), listener)
    }

    fun skipInvite(pageID: Long, listener: ICNewApiListener<ICResponse<Boolean>>) {
        val param = hashMapOf<String, Any>()
        param["pageId"] = pageID
        requestNewApi(ICNetworkClient.getNewSocialApi().skipInivteUser(param), listener)
    }
}
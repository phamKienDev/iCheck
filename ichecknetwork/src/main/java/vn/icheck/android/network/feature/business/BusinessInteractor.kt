package vn.icheck.android.network.feature.business

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.bookmark.ICBookmarkPage

class BusinessInteractor : BaseInteractor() {

    fun getListBusiness(listener: ICApiListener<ICListResponse<ICBusiness>>) {
        val queries = hashMapOf<String, String>()
        queries["featured"] = "1"

        requestApi(ICNetworkClient.getApiClient().getListBusiness(queries), listener)
    }

    fun getInformationBusiness(id: Long, listener: ICApiListener<ICBusinessHeader>) {
        requestApi(ICNetworkClient.getApiClient().getInformationBusiness(id), listener)
    }

    fun getProductCategory(id: Long, listener: ICApiListener<ICListResponse<ICCategory>>) {
        requestApi(ICNetworkClient.getApiClient().getProductCategory(id), listener)
    }

    fun getListProductBusiness(id: Long, offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICProduct>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = limit
        params["page_id"] = id
        params["empty_image"] = 0

        requestApi(ICNetworkClient.getApiClient().getListProduct(params), listener)
    }

    fun getProduct(page_id: Long?, category_id: Long, offset: Int, limit: Int, listener: ICApiListener<ICListResponse<ICProduct>>) {
        val params = hashMapOf<String, Any>()
        params["offset"] = offset
        params["limit"] = limit
        params["category_id"] = category_id
        if (page_id != null) {
            params["page_id"] = page_id
        }

        requestApi(ICNetworkClient.getApiClient().getListProduct(params), listener)
    }

    fun getBookmark(listener: ICApiListener<ICBookmarkPage>) {
        requestApi(ICNetworkClient.getApiClient().bookmarks, listener)
    }

    fun postBookmark(page_id: Long, listener: ICApiListener<ICBookmarkPage.Rows>) {
        val body = hashMapOf<String, Any>()
        body["page_id"] = page_id
        requestApi(ICNetworkClient.getApiClient().postBookmarks(body), listener)
    }

    fun deleteBookmark(bookmark_id: Long, listener: ICApiListener<ICBookmarkPage>) {
        requestApi(ICNetworkClient.getApiClient().deleteBookmarks(bookmark_id), listener)
    }
}
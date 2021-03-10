package vn.icheck.android.network.feature.shop

import okhttp3.ResponseBody
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.models.bookmark.ICBookmarkShop

class ShopInteractor : BaseInteractor() {

    fun getShopVariant(barcode: String, lat: Double, lng: Double, listener: ICApiListener<ICShopVariant>) {
        val params = hashMapOf<String, Any>()
        params["lat"] = lat
        params["lon"] = lng

        requestApi(ICNetworkClient.getApiClient().getListShopVariant(barcode, params), listener)
    }

    fun getShopDetail(id: Long, listener: ICApiListener<ICShop>) {
        requestApi(ICNetworkClient.getApiClient().getShopDetail(id), listener)
    }

    fun getShopProducts(id: Long,
                        offset: Int?,
                        limit: Int?,
                        order: String?,
                        price: String?,
                        search: String?,
                        type: String,
                        blocked: Boolean,
                        listener: ICApiListener<ICListResponse<ICProduct>>) {
        val params = hashMapOf<String, Any?>()
        if (offset != null) {
            params["offset"] = offset
        }
        if (limit != null) {
            params["limit"] = limit
        }
        params["shop_id"] = id
        if (order != null) {
            params["order"] = order
        }

        if (price != null) {
            params["price_range"] = price
        }

        if (search != null) {
            params["search"] = search
        }

        params["type"] = type
        params["blocked"] = blocked

        requestApi(ICNetworkClient.getApiClient().getShopProduct(params), listener)
    }

    fun getFollow(listener: ICApiListener<ICBookmarkShop>) {
        requestApi(ICNetworkClient.getApiClient().follow, listener)
    }

    fun postFollow(shop_id: Long, listener: ICApiListener<ICBookmarkShop.Rows>) {
        val body = hashMapOf<String, Any?>()
        body["shop_id"] = shop_id
        requestApi(ICNetworkClient.getApiClient().postFollowShop(body), listener)
    }

    fun unFollow(bookmark_id: Long, listener: ICApiListener<ResponseBody>) {
        requestApi(ICNetworkClient.getApiClient().deleteFollowShop(bookmark_id), listener)
    }

    fun reviewShop(body: ICReqShopReview, listener: ICApiListener<ICRespShopReview>) {
        requestApi(ICNetworkClient.getApiClient().reviewShop(body), listener)
    }

    fun getShopCriterias(shopID: Long, listener: ICApiListener<ICListResponse<ICCriteriaShop>>) {
        requestApi(ICNetworkClient.getApiClient().getShopCriterias(shopID), listener)
    }
}
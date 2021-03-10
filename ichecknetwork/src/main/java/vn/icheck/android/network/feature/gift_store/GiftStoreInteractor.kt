package vn.icheck.android.network.feature.gift_store

import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICCartSocial
import vn.icheck.android.network.models.ICDetailGiftStore
import vn.icheck.android.network.models.ICListGifExchange
import vn.icheck.android.network.models.ICStoreiCheck
import java.util.HashMap

class GiftStoreInteractor : BaseInteractor() {

    fun getCartUser(listener: ICNewApiListener<ICResponse<MutableList<ICCartSocial>>>) {
        val url = APIConstants.socialHost + "social/api/cms/order/cart"
        requestNewApi(ICNetworkClient.getSocialApi().getCart(url), listener)
    }

    fun getListGiftStore(page: Int, listener: ICApiListener<ICListResponse<ICListGifExchange>>) {
        val fields = HashMap<String, Any>()
        fields["offset"] = page
        fields["limit"] = APIConstants.LIMIT
        requestApiDelay(ICNetworkClient.getApiClient().getListGiftStore(fields), listener)
    }

    fun getDetailGiftStore(id: Long?, listener: ICApiListener<ICDetailGiftStore>) {
        val params = HashMap<String, Any>()
        params["id"] = id.toString()
        requestApi(ICNetworkClient.getApiClient().getDetailGiftStore(params), listener)
    }

    fun getListStore(offset: Int, listener: ICNewApiListener<ICResponse<ICListResponse<ICStoreiCheck>>>) {
        val params = HashMap<String, Any>()
        params["offset"] = offset
        params["limit"] = APIConstants.LIMIT

        requestNewApi(ICNetworkClient.getSocialApi().getListStore(params), listener)
    }

    fun getDetailStore(id: Long, listener: ICNewApiListener<ICResponse<ICStoreiCheck>>) {
        requestNewApi(ICNetworkClient.getSocialApi().getDetailStore(id), listener)
    }

    fun addToCart(name: String, id: Long, originId: Long, imageUrl: String, price: Long, listener: ICNewApiListener<ICResponse<Int>>) {
        val product = hashMapOf<String, Any>()
        product["name"] = name
        product["id"] = id
        product["originId"] = originId
        product["imageUrl"] = imageUrl

        val params = hashMapOf<String, Any>()
        params["product"] = product
        params["price"] = price
        params["originPrice"] = price
        params["quantity"] = 1

        requestNewApi(ICNetworkClient.getSocialApi().addToCart(params), listener)
    }
}
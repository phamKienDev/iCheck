package vn.icheck.android.network.feature.cart

import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICNetworkClient
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.feature.base.BaseInteractor
import vn.icheck.android.network.models.ICRespCart

class CartInteractor : BaseInteractor() {

    fun addCart(item_id: Long, quantity: Int, listener: ICApiListener<ICRespCart>) {
        val params = hashMapOf<String, Any>()
        params["item_id"] = item_id
        params["quantity"] = quantity
        requestApi(ICNetworkClient.getApiClient().addCart(params), listener)
    }

    fun getListCart(listener: ICApiListener<ICRespCart>) {
//        val url = APIConstants.socialHost + APIConstants.Cart.CART_ITEMS
//        requestApi(ICNetworkClient.getApiClient().listCart(url), listener)
    }
}
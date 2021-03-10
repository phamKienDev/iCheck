package vn.icheck.android.screen.user.checkoutcart.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.user.checkoutcart.entity.Checkout

/**
 * Created by VuLCL on 12/24/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface ICheckoutCartView : BaseActivityView {

    fun onShowLoading()
    fun onCloseLoading()

    fun onCheckoutError(error: String, body: ICReqCheckout?, isFirstCreate: Boolean)
    fun onSetCheckout(list: MutableList<Checkout>, grandTotal: Long)

    fun onMessageClicked()
    fun onAddUserAddress()
    fun onSelectUserAddress(addressID: Long)
    fun onChangeUserAddress(addressID: Long)

    fun onChangeShippingUnit(shopID: Long, shippingID: Int, list: MutableList<ICShipping>)
    fun onChangePayment(obj: ICPayment)

    fun onCompleteCheckoutError(error: String)
    fun onCompleteCheckoutSuccess(url: String?)

    fun onShopClicked(shopID: Long)
    fun onProductClicked(obj: ICItemCart)
}
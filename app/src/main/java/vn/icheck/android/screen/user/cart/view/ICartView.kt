package vn.icheck.android.screen.user.cart.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.room.entity.ICCart

/**
 * Created by VuLCL on 12/11/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface ICartView : IRecyclerViewCallback, BaseActivityView {

    fun onSetError(icon: Int, message: String)
    fun onSetListCart(list: MutableList<ICCart>)

    fun onChangeQuantity(obj: ICItemCart, count: Int, parentPosition: Int, childPosition: Int)
    fun onNotEnoughInStock()

    fun onUpdateCart(obj: ICItemCart, parentPosition: Int, childPosition: Int)
    fun onRefreshCart(obj: ICItemCart, parentPosition: Int, childPosition: Int)
    fun onRemoveCart(parentPosition: Int, childPosition: Int)

    fun updateTotalMoney()
    fun onSkipCart(cartID: Long, position: Int)
    fun onClickShopDetail(shopId: Long)
    fun onClickShopProductDetail(productId: Long)
}
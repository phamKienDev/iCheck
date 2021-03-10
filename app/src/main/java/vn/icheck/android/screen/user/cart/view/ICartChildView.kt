package vn.icheck.android.screen.user.cart.view

import vn.icheck.android.network.models.ICItemCart

interface ICartChildView {

    fun onUpdateItemQuantity(obj: ICItemCart, count: Int, position: Int)
    fun onNotEnoughInStock()
    fun onClickItemProduct(productId: Long)
    fun onSkipItem(isSkipAll: Boolean)
}
package vn.icheck.android.screen.user.scanbuy.view

import vn.icheck.android.base.activity.BaseActivityView
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.network.models.ICShopVariant

/**
 * Created by VuLCL on 12/3/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
interface IScanBuyView : BaseActivityView {

    fun onUpdateListCart(list: MutableList<ICItemCart>, totalPrice: String, totalItem: String?)
//    fun onUpdateTotalPrice(price: String)
    fun onGetShopSuccess(obj: ICShopVariant)
    fun onAddToCart(obj: ICShopVariant, count: Int)
    fun onAddToCartSuccess(id: Long)
    fun onClickDetailProduct(obj: ICShopVariant)
    fun onClickShopDetail(obj: ICShopVariant)
    fun onUpdateView()
}
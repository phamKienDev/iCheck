package vn.icheck.android.screen.user.scanbuy.presenter

import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.model.interactor.LocalCartInteractor
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.feature.shop.ShopInteractor
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.network.models.ICRespCart
import vn.icheck.android.network.models.ICShopVariant
import vn.icheck.android.screen.user.scanbuy.view.IScanBuyView

/**
 * Created by VuLCL on 12/3/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class ScanBuyPresenter(val view: IScanBuyView) : BaseActivityPresenter(view) {
    private val scanBuyInteraction = LocalCartInteractor()
    private val cartInteraction = CartInteractor()
    private val shopInteraction = ShopInteractor()
    private val cartHelper = CartHelper()

    fun findShop(barcode: String, lat: Double, lng: Double) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        shopInteraction.getShopVariant(barcode, lat, lng, object : ICApiListener<ICShopVariant> {
            override fun onSuccess(obj: ICShopVariant) {
                view.onGetShopSuccess(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                showError(getString(R.string.quanh_day_chua_co_cua_hang_nao_ban_san_pham_nay))
            }
        })
    }

    fun getListCartOffline() {
        scanBuyInteraction.getListItemCartOffline(object : ICApiListener<MutableList<ICItemCart>> {
            override fun onSuccess(obj: MutableList<ICItemCart>) {
                var totalPrice = 0L
                var totalItem = 0

                for (it in obj) {
                    totalPrice += it.quantity * it.price
                    totalItem += it.quantity
                }

                val mTotalItem = if (totalItem > 0) {
                    if (totalItem > 99) {
                        "99+"
                    } else {
                        totalItem.toString()
                    }
                } else {
                    null
                }

                view.onUpdateListCart(obj, TextHelper.formatMoney(totalPrice), mTotalItem)
//                view.onUpdateTotalPrice(TextHelper.formatMoney(cartHelper.totalItemPrice(obj)))
            }

            override fun onError(error: ICBaseResponse?) {

            }
        })
    }

    fun updateCountCart() {
        cartHelper.updateCountCart()
    }

    fun addToCart(id: Long, count: Int) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        cartInteraction.addCart(id, count, object : ICApiListener<ICRespCart> {
            override fun onSuccess(obj: ICRespCart) {
                cartHelper.saveCart(obj)
                view.onAddToCartSuccess(id)
            }

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }

    fun disposeApi() {
        scanBuyInteraction.dispose()
        cartInteraction.dispose()
        shopInteraction.dispose()
        cartHelper.dispose()
    }
}
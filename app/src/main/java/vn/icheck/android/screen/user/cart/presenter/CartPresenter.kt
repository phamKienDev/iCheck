package vn.icheck.android.screen.user.cart.presenter

import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.model.interactor.LocalCartInteractor
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.network.models.ICRespCart
import vn.icheck.android.room.entity.ICCart
import vn.icheck.android.screen.user.cart.view.ICartView

/**
 * Created by VuLCL on 12/11/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CartPresenter(val view: ICartView) : BaseActivityPresenter(view) {
    private val localCartInteraction = LocalCartInteractor()
    private val serverCartInteraction = CartInteractor()
    private val cartHelper = CartHelper()

    fun getCartOffline() {
        localCartInteraction.getListCartsOffline(object : ICApiListener<MutableList<ICCart>> {
            override fun onSuccess(obj: MutableList<ICCart>) {
                view.onSetListCart(obj)
            }

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.khong_the_truy_xuat_du_lieu_vui_long_thu_lai)
                view.onSetError(R.drawable.ic_error_request, message)
            }
        })
    }

    fun getCartOnline() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onSetError(R.drawable.ic_error_network, getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        serverCartInteraction.getListCart(object : ICApiListener<ICRespCart> {
            override fun onSuccess(obj: ICRespCart) {
                cartHelper.saveCart(obj)
                getCartOffline()
            } 

            override fun onError(error: ICBaseResponse?) {
                val message = error?.message ?: getString(R.string.khong_the_truy_xuat_du_lieu_vui_long_thu_lai)
                view.onSetError(R.drawable.ic_error_request, message)
            }
        })
    }

    fun updateItemQuantity(cart: ICItemCart, count: Int, parentPosition: Int, childPosition: Int) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            showError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            view.onRefreshCart(cart, parentPosition, childPosition)
            return
        }

        serverCartInteraction.addCart(cart.item_id, count, object : ICApiListener<ICRespCart> {
            override fun onSuccess(obj: ICRespCart) {
                cartHelper.saveCart(obj)

                if (count > 0) {
                    view.onUpdateCart(cart, parentPosition, childPosition)
                } else {
                    view.onRemoveCart(parentPosition, childPosition)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onRefreshCart(cart, parentPosition, childPosition)
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                showError(message)
            }
        })
    }

    fun disposeApi() {
        localCartInteraction.dispose()
        serverCartInteraction.dispose()
    }
}
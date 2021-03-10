package vn.icheck.android.screen.user.orderdetail.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.feature.checkout.CheckoutInteractor
import vn.icheck.android.network.feature.order.OrderInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.screen.user.orderdetail.view.IOrderDetailView

/**
 * Created by VuLCL on 1/31/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class OrderDetailPresenter(val view: IOrderDetailView) : BaseActivityPresenter(view) {
    private val orderInteraction = OrderInteractor()
    private val cartInteractor = CartInteractor()
    private val cartHelper = CartHelper()

    private val listCart = mutableListOf<ICItemCart>()

    private var orderID: Long = -1

    fun getID(intent: Intent?) {
        orderID = try {
            intent?.getLongExtra(Constant.DATA_1, -1) ?: -1
        } catch (e: Exception) {
            -1
        }

        getOrderDetail(true)
    }

    fun getOrderDetail(isShowLoading: Boolean) {
        if (orderID == -1L) {
            view.onGetIDError()
            return
        }

        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onGetDetailError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        if (isShowLoading)
            view.onShowLoading()

        orderInteraction.getOrderDetail(orderID, object : ICApiListener<ICOrderDetail> {
            override fun onSuccess(obj: ICOrderDetail) {
                view.onCloseLoading()

                val list = mutableListOf<Int>()
                list.add(1)
                list.add(2)

                if (obj.shipping_address != null) {
                    list.add(3)
                }

                list.add(4)
                list.add(5)
                list.add(6)

                view.onDetailSuccess(obj, list)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.khong_the_truy_xuat_du_lieu_vui_long_thu_lai)
                view.onGetDetailError(message)
            }
        })
    }

    fun payAgain(orderID: Long) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onShowNotification(getString(R.string.icheck_thong_bao), getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        view.onShowLoading()

        orderInteraction.payOrder(orderID, object : ICApiListener<ICRespCheckoutCart> {
            override fun onSuccess(obj: ICRespCheckoutCart) {
                view.onCloseLoading()
                view.onPaySuccess(obj.url)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onShowNotification(getString(R.string.icheck_thong_bao), message)
            }
        })
    }

    fun cancelOrder(orderID: Long, reason: String) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onShowNotification(getString(R.string.icheck_thong_bao), getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        view.onShowLoading()

//        orderInteraction.cancelOrder(orderID, reason, object : ICApiListener<ICRespID> {
//            override fun onSuccess(obj: ICRespID) {
//                view.onCloseLoading()
//                view.onCancelSuccess(orderID)
//                getOrderDetail(false)
//                view.onShowNotification(getString(R.string.icheck_thong_bao), getString(R.string.huy_don_hang_thanh_cong))
//            }
//
//            override fun onError(error: ICBaseResponse?) {
//                view.onCloseLoading()
//                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
//                view.onShowNotification(getString(R.string.icheck_thong_bao), message)
//            }
//        })
    }

    fun completeOrder(orderID: Long) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onShowNotification(getString(R.string.icheck_thong_bao), getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        orderInteraction.completeOrder(orderID, object : ICApiListener<ICOrderHistory> {
            override fun onSuccess(obj: ICOrderHistory) {
                view.onCloseLoading()
                view.onCompleteSuccess(orderID)
                getOrderDetail(false)
                view.onShowNotification(getString(R.string.icheck_thong_bao), getString(R.string.nhan_hang_thanh_cong))
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onShowNotification(getString(R.string.icheck_thong_bao), message)
            }
        })
    }

    fun orderAgain(obj: ICOrderDetail) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onShowNotification(getString(R.string.icheck_thong_bao), getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        stopOrderAgain()

        for (it in obj.items) {
            if (it.can_add_to_cart == true) {
                listCart.add(it)
            }
        }

        if (listCart.isEmpty()) {
            view.onShowNotification(getString(R.string.loi), getString(R.string.order_again_can_not_add))
        } else {
            view.onShowLoading()
            startOrderAgain()
        }
    }

    fun startOrderAgain() {
        if (listCart.isEmpty()) {
            view.onCloseLoading()
            view.onOrderAgainSuccess()
        }

        cartInteractor.addCart(listCart[0].item_id, listCart[0].quantity, object : ICApiListener<ICRespCart> {
            override fun onSuccess(obj: ICRespCart) {
                cartHelper.saveCart(obj)
                listCart.removeAt(0)
                startOrderAgain()
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                view.onOrderAgainError()
            }
        })
    }

    fun stopOrderAgain() {
        listCart.clear()
    }
}
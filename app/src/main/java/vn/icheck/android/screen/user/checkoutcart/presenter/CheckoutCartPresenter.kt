package vn.icheck.android.screen.user.checkoutcart.presenter

import android.content.Intent
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityPresenter
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.model.interactor.LocalCartInteractor
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.feature.checkout.CheckoutInteractor
import vn.icheck.android.network.feature.user.UserInteractor
import vn.icheck.android.network.models.*
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.room.entity.ICCart
import vn.icheck.android.screen.user.cart.adapter.CartParentAdapter
import vn.icheck.android.screen.user.checkoutcart.adapter.CheckoutCartAdapter
import vn.icheck.android.screen.user.checkoutcart.entity.Checkout
import vn.icheck.android.screen.user.checkoutcart.view.ICheckoutCartView

/**
 * Created by VuLCL on 12/24/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class CheckoutCartPresenter(val view: ICheckoutCartView) : BaseActivityPresenter(view) {
    private val userInteraction = UserInteractor()
    private val checkoutInteraction = CheckoutInteractor()
    private val cartInteraction = LocalCartInteractor()

    private val listAddress = mutableListOf<ICAddress>()

    fun refreshData() {
        listAddress.clear()
        checkAction()
    }

    fun checkAction() {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onCheckoutError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), null, true)
            return
        }

        view.onShowLoading()


        if (listAddress.isEmpty()) {
            getAddresses()
        } else {
            getCartsOffline()
        }
    }

    val getListAddressJson: String
        get() {
            return JsonHelper.toJson(listAddress)
        }

    private fun getAddresses() {
        userInteraction.getListUserAddress(object : ICApiListener<ICListResponse<ICAddress>> {
            override fun onSuccess(obj: ICListResponse<ICAddress>) {
                listAddress.clear()
                listAddress.addAll(obj.rows)
                getCartsOffline()
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                view.onCheckoutError(getError(error?.message), null, true)
            }
        })
    }

    fun getCartsOffline() {
        cartInteraction.getListCartsOffline(object : ICApiListener<MutableList<ICCart>> {
            override fun onSuccess(obj: MutableList<ICCart>) {
                if (obj.isEmpty()) {
                    view.onCloseLoading()
                    view.onSetCheckout(mutableListOf(), 0)
                } else {
                    createCheckouts(getBody(obj), true)
                }
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                view.onCheckoutError(getError(error?.message), null, true)
            }
        })
    }

    fun createCheckouts(addressID: Long?, paymentID: Int?, shopID: Long?, shippingID: Int?, list: MutableList<Checkout>, isChangeAddress: Boolean) {
        val body = if (isChangeAddress) {
            getChangeAddressBody(addressID, paymentID, list)
        } else {
            getBody(addressID, paymentID, shopID, shippingID, list)
        }
        createCheckouts(body, false)
    }

    fun createCheckouts(body: ICReqCheckout, isFirstCreate: Boolean) {
        if (!isFirstCreate) {
            if (NetworkHelper.isNotConnected(view.mContext)) {
                view.onCheckoutError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), body, false)
                return
            }

            view.onShowLoading()
        }

        checkoutInteraction.createCheckout(body, object : ICApiListener<ICCheckout> {
            override fun onSuccess(obj: ICCheckout) {
                view.onCloseLoading()

                val list = mutableListOf<Checkout>()
                list.addAll(getAddressObj(obj.shipping_address_id))
                list.addAll(getOrderObj(obj.orders))
                list.addAll(getPaymentObj(obj.payment_method_id, obj.payment_methods))
                list.addAll(getMoneyObj(obj))

                view.onSetCheckout(list, obj.grand_total)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                view.onCheckoutError(getError(error?.message), body, false)
            }
        })
    }

    fun completeCheckouts(list: MutableList<Checkout>) {
        if (NetworkHelper.isNotConnected(view.mContext)) {
            view.onCompleteCheckoutError(getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        val body = getBody(null, null, null, null, list)

        if (body.shipping_address_id == null) {
            view.onCompleteCheckoutError(getString(R.string.chua_chon_dia_chi_order))
            return
        }

        view.onShowLoading()

        checkoutInteraction.completeCheckout(body, object : ICApiListener<ICRespCheckoutCart> {
            override fun onSuccess(obj: ICRespCheckoutCart) {
                view.onCloseLoading()
                view.onCompleteCheckoutSuccess(obj.url)
                CartHelper().clearCart(view.mContext)
                CartHelper().clearCartSocial(view.mContext)
            }

            override fun onError(error: ICBaseResponse?) {
                view.onCloseLoading()
                val message = error?.message ?: getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                view.onCompleteCheckoutError(message)
            }
        })
    }

    private fun getBody(obj: MutableList<ICCart>): ICReqCheckout {
        val listOrder = mutableListOf<ICReqOrder>()

        for (it in obj) {
            if (CartParentAdapter.skipCart[it.id] == true) {
                continue
            }

            val listItems = mutableListOf<ICReqItem>()

            for (item in it.items) {
                if (item.can_add_to_cart == true && item.stock > 0 && CartParentAdapter.skipCart[item.item_id] != true) {
                    listItems.add(ICReqItem(item.item_id))
                }
            }

            if (listItems.isNotEmpty()) {
                val order = ICReqOrder(it.shop_id, null, listItems, null)
                listOrder.add(order)
            }
        }

        return ICReqCheckout(defaultAddress(null)?.id, null, listOrder)
    }

    private fun getBody(addressID: Long?, paymentID: Int?, shopID: Long?, shippingID: Int?, listCheckouts: MutableList<Checkout>): ICReqCheckout {
        val listOrder = mutableListOf<ICReqOrder>()

        var shippingAddressID: Long? = null
        var paymentMethodID: Int? = null

        for (checkout in listCheckouts) {
            when (checkout.type) {
                CheckoutCartAdapter.addressType -> {
                    shippingAddressID = addressID ?: checkout.address?.id
                }
                CheckoutCartAdapter.orderType -> {
                    val listItems = mutableListOf<ICReqItem>()

                    val order = checkout.order!!
                    for (item in order.items) {
                        listItems.add(ICReqItem(item.item_id))
                    }

                    val shippingMethodID = if (shippingID != null) {
                        if (shopID == order.shop_id) {
                            shippingID
                        } else {
                            order.shipping_method_id
                        }
                    } else {
                        order.shipping_method_id
                    }

                    listOrder.add(ICReqOrder(order.shop_id, shippingMethodID, listItems, checkout.order!!.note))
                }
                CheckoutCartAdapter.paymentType -> {
                    paymentMethodID = paymentID ?: checkout.payment_method_id
                }
            }
        }

        return ICReqCheckout(shippingAddressID, paymentMethodID, listOrder)
    }

    private fun getChangeAddressBody(addressID: Long?, paymentID: Int?, listCheckouts: MutableList<Checkout>): ICReqCheckout {
        val listOrder = mutableListOf<ICReqOrder>()

        var shippingAddressID: Long? = null
        var paymentMethodID: Int? = null

        for (checkout in listCheckouts) {
            when (checkout.type) {
                CheckoutCartAdapter.addressType -> {
                    shippingAddressID = addressID ?: checkout.address?.id
                }
                CheckoutCartAdapter.orderType -> {
                    val listItems = mutableListOf<ICReqItem>()

                    val order = checkout.order!!
                    for (item in order.items) {
                        listItems.add(ICReqItem(item.item_id))
                    }

                    listOrder.add(ICReqOrder(order.shop_id, null, listItems, checkout.order!!.note))
                }
                CheckoutCartAdapter.paymentType -> {
                    paymentMethodID = paymentID ?: checkout.payment_method_id
                }
            }
        }

        return ICReqCheckout(shippingAddressID, paymentMethodID, listOrder)
    }

    private fun getAddressObj(addressID: Long?): MutableList<Checkout> {
        val list = mutableListOf<Checkout>()

        val headerAction: Int?
        val addressType: Int

        val addressObj = defaultAddress(addressID)
        if (addressObj != null) {
            headerAction = R.string.thay_doi
            addressType = CheckoutCartAdapter.addressType
        } else {
            headerAction = null
            addressType = CheckoutCartAdapter.addAddressType
        }

        val header = Checkout(CheckoutCartAdapter.titleType)
        header.title = R.string.dia_chi_nhan_hang
        header.action = headerAction
        header.shipping_address_id = addressObj?.id
        list.add(header)

        val address = Checkout(addressType)
        address.address = addressObj
        list.add(address)

        return list
    }

    private fun defaultAddress(addressID: Long?): ICAddress? {
        for (it in listAddress) {
            if (addressID != null) {
                if (it.id == addressID) {
                    return it
                }
            } else {
                if (it.is_default == true) {
                    return it
                }
            }
        }

        return if (listAddress.isNotEmpty()) {
            listAddress[0]
        } else {
            null
        }
    }

    private fun getOrderObj(obj: MutableList<ICCheckoutOrder>): MutableList<Checkout> {
        val list = mutableListOf<Checkout>()

        val header = Checkout(CheckoutCartAdapter.titleType)
        header.title = R.string.thong_tin_san_pham
        list.add(header)

        for (it in obj) {
            val order = Checkout(CheckoutCartAdapter.orderType)
            order.order = it
            list.add(order)
        }

        return list
    }

    private fun getPaymentObj(paymentID: Int, obj: MutableList<ICPayment>): MutableList<Checkout> {
        val list = mutableListOf<Checkout>()

        val header = Checkout(CheckoutCartAdapter.titleType)
        header.title = R.string.hinh_thuc_thanh_toan
        list.add(header)

        val payment = Checkout(CheckoutCartAdapter.paymentType)
        payment.payments = obj
        payment.payment_method_id = paymentID
        list.add(payment)

        return list
    }

    private fun getMoneyObj(obj: ICCheckout): MutableList<Checkout> {
        val list = mutableListOf<Checkout>()

        val header = Checkout(CheckoutCartAdapter.titleType)
        header.title = R.string.tong_tien_phai_tra
        list.add(header)

        val money = Checkout(CheckoutCartAdapter.moneyType)
        money.sub_total = obj.sub_total
        money.shipping_amount = obj.shipping_amount
        money.grand_total = obj.grand_total
        list.add(money)

        return list
    }

    fun createAddress(intent: Intent?) {
        val address = try {
            JsonHelper.parseJson(intent?.getStringExtra(Constant.DATA_1), ICAddress::class.java)
        } catch (e: Exception) {
            null
        }

        address?.let {
            for (i in listAddress.size - 1 downTo 0) {
                listAddress[i].is_default = false
            }
            //

            it.is_default = true

            if (listAddress.isNotEmpty()) {
                listAddress.add(0, it)
            } else {
                listAddress.add(it)
            }

            view.onChangeUserAddress(it.id!!)
        }
    }

    fun selectAddress(intent: Intent?) {
        val obj = JsonHelper.parseJson(intent?.getStringExtra(Constant.DATA_1), ICAddress::class.java)

        if (obj == null) {
            listAddress.clear()
            checkAction()
        } else {
            obj.is_default = true
            listAddress.clear()
            listAddress.add(obj)

            view.onChangeUserAddress(obj.id!!)
        }
    }

    private fun getError(message: String?): String {
        return message ?: getString(R.string.khong_the_truy_xuat_du_lieu_vui_long_thu_lai)
    }

    fun disposeApi() {
        userInteraction.dispose()
        checkoutInteraction.dispose()
        cartInteraction.dispose()
    }
}
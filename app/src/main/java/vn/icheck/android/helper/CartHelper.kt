package vn.icheck.android.helper

import android.content.Context
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.feature.gift_store.GiftStoreInteractor
import vn.icheck.android.network.models.ICCartSocial
import vn.icheck.android.network.models.ICItemCart
import vn.icheck.android.network.models.ICRespCart
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.room.entity.ICCart
import vn.icheck.android.room.entity.ICProductIdInCart

class CartHelper {
    private val interaction = CartInteractor()

    fun saveCart(respCart: ICRespCart) {
        ICheckApplication.getInstance().applicationContext?.let {
            val cartDao = AppDatabase.getDatabase(it).cartDao()
            cartDao.deleteAll()

            for (orders in respCart.orders) {
                val cart = ICCart(orders.id, orders.shop_id, orders.shop, orders.items)
                cartDao.insertCart(cart)
            }
        }
    }

    fun clearCart(context: Context) {
        val cartDao = AppDatabase.getDatabase(context).cartDao()
        cartDao.deleteAll()
    }

    fun getCartSocial(){
        if (NetworkHelper.isConnected(ICheckApplication.getInstance())){
            ICheckApplication.getInstance().applicationContext?.let {
                val productIdCart = AppDatabase.getDatabase(it).productIdInCartDao()
                productIdCart.deleteAll()

                GiftStoreInteractor().getCartUser(object : ICNewApiListener<ICResponse<MutableList<ICCartSocial>>> {
                    override fun onSuccess(obj: ICResponse<MutableList<ICCartSocial>>) {
                        if (!obj.data.isNullOrEmpty()) {
                            for (item in obj.data!!.firstOrNull()?.products ?: mutableListOf()) {
                                if (item.product?.id != null) {
                                    productIdCart.insertProduct(ICProductIdInCart(item.product?.id!!, (item.price ?: 0) * (item.quantity ?: 1), (item.quantity ?: 1).toLong()))
                                }
                            }
                        }
                    }

                    override fun onError(error: ICResponseCode?) {

                    }
                })
            }
        }
    }

    fun clearCartSocial(context: Context){
        AppDatabase.getDatabase(context).productIdInCartDao().deleteAll()
    }

    fun totalCartPrice(list: MutableList<ICCart>): Long {
        var price = 0L

        for (cart in list) {
            for (item in cart.items) {
                price += (item.price * item.quantity)
            }
        }

        return price
    }

    fun totalItemPrice(list: MutableList<ICItemCart>): Long {
        var price = 0L

        for (it in list) {
            price += it.quantity * it.price
        }

        return price
    }

    fun totalItemQuantity(list: MutableList<ICItemCart>): Int {
        var quantity = 0

        for (it in list) {
            quantity += it.quantity
        }

        return quantity
    }

    fun updateCountCart() {
        if (SessionManager.isLoggedAnyType) {
            interaction.getListCart(object : ICApiListener<ICRespCart> {
                override fun onSuccess(obj: ICRespCart) {
                    if (SessionManager.isLoggedAnyType) {
                        saveCart(obj)
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_CART, when {
                            obj.item_total > 9 -> {
                                "9+"
                            }
                            obj.item_total > 0 -> {
                                obj.item_total.toString()
                            }
                            else -> {
                                null
                            }
                        }))
                    }
                }

                override fun onError(error: ICBaseResponse?) {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_CART, null))
                }
            })
        } else {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_CART, null))
        }
    }

    fun dispose() {
        interaction.dispose()
    }
}
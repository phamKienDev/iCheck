package vn.icheck.android.helper

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.models.ICRespCart
import vn.icheck.android.util.kotlin.ToastUtils

object AddToCartHelper {
    private val cartInteraction = CartInteractor()
    private val cartHelper = CartHelper()

    fun addToCart(context: Context, id: Long?) {
        if (NetworkHelper.isNotConnected(context)) {
            ToastUtils.showShortError(context, context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        id?.let { it ->
            cartInteraction.addCart(it, 1, object : ICApiListener<ICRespCart> {
                override fun onSuccess(obj: ICRespCart) {
                    cartHelper.saveCart(obj)
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_CART))
                    ToastUtils.showShortSuccess(context, context.getString(R.string.them_vao_gio_hang_thanh_cong))
                }

                override fun onError(error: ICBaseResponse?) {
                    val message = error?.message
                            ?: context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    ToastUtils.showShortError(context, message)
                }
            })
        }
    }

    fun updateInfo(tvCartCount: AppCompatTextView) {
        if (SessionManager.isUserLogged) {
            cartInteraction.getListCart(object : ICApiListener<ICRespCart> {
                override fun onSuccess(obj: ICRespCart) {
                    if (SessionManager.isUserLogged) {
                        tvCartCount?.run {
                            visibility = if (obj.item_total != null) {
                                View.VISIBLE
                            } else {
                                View.INVISIBLE
                            }

                            text = when {
                                obj.item_total > 9 -> {
                                    "9+"
                                }
                                obj.item_total > 0 -> {
                                    obj.item_total.toString()
                                }
                                else -> {
                                    null
                                }
                            }
                        }
                    }
                }

                override fun onError(error: ICBaseResponse?) {

                }
            })
        } else {
            tvCartCount.visibility = View.INVISIBLE
        }
    }
}
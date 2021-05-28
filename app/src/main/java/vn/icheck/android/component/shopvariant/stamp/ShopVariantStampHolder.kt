package vn.icheck.android.component.shopvariant.stamp

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.layout_base_shop_variant.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.models.ICRespCart
import vn.icheck.android.network.models.ICShopVariant
import vn.icheck.android.network.models.detail_stamp_v6_1.ICServiceShopVariant
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter.ServiceShopVariantAdapter
import vn.icheck.android.util.kotlin.ToastUtils

class ShopVariantStampHolder(view: View) : BaseViewHolder<ICShopVariant>(view) {

    private val cartInteraction = CartInteractor()
    private val cartHelper = CartHelper()

    private var km: String? = null

    private lateinit var adapterService: ServiceShopVariantAdapter

    @SuppressLint("SetTextI18n")
    override fun bind(obj: ICShopVariant) {
        listener(obj)
        val tvNameShopVariant = itemView.tvNameShopVariant as AppCompatTextView
        val tvKmShopVariant = itemView.tvKmShopVariant as AppCompatTextView
        val tvRatingShopVariant = itemView.tvRatingShopVariant as AppCompatTextView
        val tvAddressShopVariant = itemView.tvAddressShopVariant as AppCompatTextView
        val tvPriceNoSale = itemView.tvPriceNoSale as AppCompatTextView
        val tvPriceSale = itemView.tvPriceSale as AppCompatTextView
        val tvAddToCartInDiemBan = itemView.tvAddToCartInDiemBan as AppCompatTextView
        val rcvListService = itemView.rcvServiceShopVariant as RecyclerView
        val constraintLayout = itemView.constraintLayout as ConstraintLayout

        constraintLayout.background=ViewHelper.bgWhiteRadiusBottom10(itemView.context)

        if (obj.shop?.is_online == true) {
            if (obj.is_active == true) {
                tvAddToCartInDiemBan.visibility = View.VISIBLE
            } else {
                tvAddToCartInDiemBan.visibility = View.INVISIBLE
            }
        }else{
            tvAddToCartInDiemBan.visibility = View.INVISIBLE
        }

        tvNameShopVariant.text = if (!obj.shop?.name.isNullOrEmpty()) {
            obj.shop?.name
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        if (obj.shop?.distance != null) {
            if (obj.shop?.distance?.value.toString().isNotEmpty()) {
                km = TextHelper.getTextAfterDot(obj.shop?.distance?.value)
            }
        }

        tvKmShopVariant.text = if (obj.shop?.distance != null) {
            km + " " + obj.shop?.distance?.unit
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        tvRatingShopVariant.text = if (obj.shop?.rating != null) {
            obj.shop?.rating.toString() + "/" + "5.0"
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        tvAddressShopVariant.text = if (!obj.shop?.address.isNullOrEmpty()) {
            obj.shop?.address + ", " + obj.shop?.ward?.name + ", " + obj.shop?.district?.name + ", " + obj.shop?.city?.name
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        if (obj.sale_off == true) {
            if (obj.special_price != null) {
                if (obj.special_price!! > 0L) {
                    tvPriceNoSale.text = TextHelper.formatMoneyPhay(obj.price)
                    tvPriceNoSale.paintFlags = tvPriceNoSale.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvPriceNoSale.visibility = View.INVISIBLE
                }
            } else {
                tvPriceNoSale.visibility = View.INVISIBLE
            }
        } else {
            tvPriceNoSale.visibility = View.INVISIBLE
        }

        tvPriceSale.text = TextHelper.formatMoneyPhay(obj.special_price) + "đ"

        val listService = mutableListOf<ICServiceShopVariant>()
        if (obj.shop?.is_online == true) {
            listService.add(ICServiceShopVariant(0, R.drawable.ic_online_shop_18px, "Bán Online", "#2d9cdb", R.drawable.bg_corner_online_shop_variant))
        }

        if (obj.verified == "verified") {
            listService.add(ICServiceShopVariant(1, R.drawable.ic_verified_18px, "Đại lý chính hãng", "#4dbba6", R.drawable.bg_corner_offline_shop_variant))
        }

        if (obj.shop?.is_offline == true) {
            listService.add(ICServiceShopVariant(2, R.drawable.ic_offline_shop_18px, "Mua tại cửa hàng", "#49aa2d", R.drawable.bg_corner_verified_shop_variant))
        }

        if (!listService.isNullOrEmpty()) {
            initAdapterService(rcvListService)
            adapterService.setListData(listService)
        }
    }

    private fun listener(obj: ICShopVariant) {
        itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
//                ShopDetailActivity.start(obj.shop_id, act)
            }
        }

        itemView.tvAddToCartInDiemBan.setOnClickListener {
            if (SessionManager.isUserLogged) {
                if (NetworkHelper.isNotConnected(itemView.context)) {
                    ToastUtils.showShortError(itemView.context, itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                    return@setOnClickListener
                }

                cartInteraction.addCart(obj.id, 1, object : ICApiListener<ICRespCart> {
                    override fun onSuccess(obj: ICRespCart) {
                        cartHelper.saveCart(obj)
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UPDATE_COUNT_CART))
                        ToastUtils.showShortSuccess(itemView.context, itemView.context.getString(R.string.them_vao_gio_hang_thanh_cong))
                    }

                    override fun onError(error: ICBaseResponse?) {
                        val message = error?.message ?: itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                        ToastUtils.showShortError(itemView.context, message)
                    }
                })
            } else {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.ON_REQUIRE_LOGIN))
            }
        }
    }

    private fun initAdapterService(rcvListService: RecyclerView) {
        adapterService = ServiceShopVariantAdapter(itemView.context)
        rcvListService.layoutManager = FlexboxLayoutManager(itemView.context, FlexDirection.ROW, FlexWrap.WRAP)
        rcvListService.adapter = adapterService
    }

    companion object {
        fun createHolder(parent: ViewGroup): ShopVariantStampHolder {
            return ShopVariantStampHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_base_shop_variant, parent, false))
        }
    }
}
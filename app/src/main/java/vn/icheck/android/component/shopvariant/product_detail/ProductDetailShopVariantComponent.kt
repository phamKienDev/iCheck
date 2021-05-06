package vn.icheck.android.component.shopvariant.product_detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.holder_shop.view.*
import kotlinx.android.synthetic.main.item_store_sell_history.view.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.CartHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beInvisible
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.cart.CartInteractor
import vn.icheck.android.network.models.ICRespCart
import vn.icheck.android.network.models.ICShopVariantV2
import vn.icheck.android.network.models.detail_stamp_v6_1.ICServiceShopVariant
import vn.icheck.android.network.models.history.ICStoreNear
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.account.home.AccountActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter.ServiceShopVariantAdapter
import vn.icheck.android.screen.user.map_scan_history.MapScanHistoryActivity
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.text.ICheckTextUtils

class ProductDetailShopVariantComponent : LinearLayout {

    constructor(context: Context) : super(context, null) {
        createView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        createView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        createView()
    }

    private val cartInteraction = CartInteractor()
    private val cartHelper = CartHelper()
    private lateinit var adapterService: ServiceShopVariantAdapter

    @SuppressLint("SetTextI18n")
    fun bind(productRow: ICShopVariantV2) {
        tv_shop_name.text = productRow.name

        if (productRow.distance != null) {
            TextHelper.convertMtoKm(productRow.distance!!, tv_distance, "Khoảng cách: ")
        } else {
            tv_distance.text = "Khoảng cách: " + context.getString(R.string.dang_cap_nhat)
        }

        tv_score.text = String.format("%.1f", productRow.rating).replace(".", ",")

        if (productRow.isOnline == true && productRow.isOffline == true) {
            layoutLocation2.visibility = View.VISIBLE
            layoutAddToCart?.visibility = View.VISIBLE

            layoutAddToCart?.layoutParams = ViewHelper.createLayoutParams32Dp(SizeHelper.size32, 0, 0, 0, 0)
            tvSubAddToCart?.compoundDrawablePadding = 30
        } else {
            layoutLocation2.visibility = View.GONE

            val param = layoutAddToCart?.layoutParams as LayoutParams?
            param?.setMargins(SizeHelper.size38, 0, 0, 0)
            layoutAddToCart?.layoutParams = param

            if (productRow.isOnline == true) {
                layoutAddToCart?.visibility = View.VISIBLE
            } else {
                layoutAddToCart?.visibility = View.GONE
            }

            layoutLocation.apply {
                if (productRow.isOffline == true) {
                    beVisible()
                } else {
                    beGone()
                }
            }
        }

        val listService = mutableListOf<ICServiceShopVariant>()

        if (productRow.isOffline == true) {
            listService.add(ICServiceShopVariant(0, R.drawable.ic_offline_shop_variant_18dp, "Mua tại cửa hàng", "#eb5757", R.drawable.bg_corner_shop_variant_offline))
        }

        if (!productRow.title.isNullOrEmpty()) {
            listService.add(ICServiceShopVariant(1, R.drawable.ic_verified_shop_variant_18px, productRow.title, "#49aa2d", R.drawable.bg_corner_verified_shop_variant))
        }

//        if (productRow.isOnline == true) {
//            listService.add(ICServiceShopVariant(2, R.drawable.ic_online_shop_variant_18px, "Bán Online", "#2d9cdb", R.drawable.bg_corner_online_shop_variant))
//        }

        if (!listService.isNullOrEmpty()) {
            initAdapterService()
            adapterService.setListData(listService)
        }

//        if (productRow.saleOff != null) {
//            if (productRow.saleOff == true) {
//                if (productRow.specialPrice != 0L) {
//                    tv_sale_price?.visibility = View.VISIBLE
//                    ICheckTextUtils.setSalePrice(tv_sale_price, productRow.price!!)z
//                    ICheckTextUtils.setPrice(tv_price, productRow.specialPrice!!)
//                } else {
//                    tv_sale_price?.visibility = View.INVISIBLE
//                    ICheckTextUtils.setPrice(tv_price, productRow.price!!)
//                }
//            } else {
//                tv_sale_price?.visibility = View.INVISIBLE
//                ICheckTextUtils.setPrice(tv_price, productRow.price!!)
//            }
//        } else {
//            tv_sale_price?.visibility = View.INVISIBLE
//            ICheckTextUtils.setPrice(tv_price, productRow.price ?: 0)
//        }

        if (productRow.price != null) {
            tv_sale_price.beVisible()
            if (productRow.saleOff == true && productRow.specialPrice != null) {
                tv_price?.beVisible()
                ICheckTextUtils.setPrice(tv_sale_price, productRow.specialPrice!!)
                ICheckTextUtils.setSalePrice(tv_price, productRow.price!!)
            } else {
                ICheckTextUtils.setPrice(tv_sale_price, productRow.price!!)
                tv_price?.beInvisible()
            }
        } else {
            tv_sale_price?.beInvisible()
            tv_price?.beInvisible()
        }

        viewLocation2.background = vn.icheck.android.ichecklibs.ViewHelper.bgOutlinePrimary1Corners4(context)

        vg_shop_top.setOnClickListener {

        }

        layoutLocation.setOnClickListener {
            showMap(productRow)
        }

        layoutLocation2.setOnClickListener {
            showMap(productRow)
        }

        layoutAddToCart.apply {
            background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                if (SessionManager.isUserLogged) {
                    if (NetworkHelper.isNotConnected(context)) {
                        ToastUtils.showShortError(context, context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                        return@setOnClickListener
                    }

                    cartInteraction.addCart(productRow.id!!, 1, object : ICApiListener<ICRespCart> {
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
                } else {
                    AccountActivity.start(context)
                }
            }
        }
    }

    private fun showMap(obj: ICShopVariantV2) {
        ICheckApplication.currentActivity()?.let { activity ->
            val json = JsonHelper.toJson(mutableListOf(ICStoreNear().apply {
                id = obj.id
                avatar = obj.avatar
                name = obj.name
                distance = obj.distance
                price = obj.price
                address = obj.address
                phone = obj.phone
                location = obj.location
            }))

            val intent = Intent(activity, MapScanHistoryActivity::class.java)
            intent.putExtra(Constant.DATA_1, json)
            intent.putExtra(Constant.DATA_2, obj.id)
            intent.putExtra(Constant.DATA_3, obj.location?.lat)
            intent.putExtra(Constant.DATA_4, obj.location?.lon)
            intent.putExtra("avatarShop", obj.avatar)
            ActivityHelper.startActivity(activity, intent)
        }

//        if (obj.location != null) {
//            if (obj.location!!.lat != 0.0 && obj.location!!.lon != 0.0) {
//                val intent = Intent(ICheckApplication.currentActivity(), MapScanHistoryActivity::class.java)
//                intent.putExtra(Constant.DATA_2, obj.id)
//                intent.putExtra(Constant.DATA_3, obj.location?.lat)
//                intent.putExtra(Constant.DATA_4, obj.location?.lon)
//                intent.putExtra("avatarShop", obj.avatar)
//                ICheckApplication.currentActivity()?.startActivity(intent)
//            } else {
//                ToastUtils.showShortError(context, "Vị trí của shop đang được cập nhật")
//            }
//        } else {
//            ToastUtils.showShortError(context, "Vị trí của shop đang được cập nhật")
//        }
    }

    private fun initAdapterService() {
        adapterService = ServiceShopVariantAdapter(context)
        recyclerView?.layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
        recyclerView?.adapter = adapterService
    }

    private fun createView() {
        addView(LayoutInflater.from(context).inflate(R.layout.holder_shop, this, false))
    }
}
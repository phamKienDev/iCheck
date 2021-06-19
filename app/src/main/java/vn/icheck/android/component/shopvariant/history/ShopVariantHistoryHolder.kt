package vn.icheck.android.component.shopvariant.history

import android.content.Intent
import android.graphics.Paint
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.layout_base_shop_variant_history.view.*
import okhttp3.ResponseBody
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.dialog.notify.map.MapGoogleActivity
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.AddToCartHelper
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.ICApiListener
import vn.icheck.android.network.base.ICBaseResponse
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.feature.history.HistoryInteractor
import vn.icheck.android.network.models.ICHistory_Product
import vn.icheck.android.network.models.detail_stamp_v6_1.ICServiceShopVariant
import vn.icheck.android.screen.account.icklogin.IckLoginActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.adapter.ServiceShopVariantAdapter
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.TestTimeUtil

class ShopVariantHistoryHolder(view: View, val listData: MutableList<ICHistory_Product?>, val listener: IShopVariantListener, val url: String) : BaseViewHolder<ICHistory_Product>(view) {

    private var interactor = HistoryInteractor()

    private var km: String? = null
    private lateinit var adapterService: ServiceShopVariantAdapter

    private val handler = Handler()

    private var isHold = false

    override fun bind(obj: ICHistory_Product) {
        initListener(obj)
        initRemove(obj)

        itemView.imgDelete.visibility = View.INVISIBLE
        itemView.progressHistory.visibility = View.INVISIBLE

        val image = obj.product?.thumbnails?.thumbnail
        if (!image.isNullOrEmpty()) {
            WidgetUtils.loadImageUrlRounded10FitCenter(itemView.imgAvaProduct, image)
        } else {
            WidgetUtils.loadImageUrlRounded10FitCenter(itemView.imgAvaProduct, null)
        }

        if (!obj.product?.name.isNullOrEmpty()) {
            itemView.tvNameProduct.text = obj.product?.name
        } else {
            itemView.tvNameProduct.text = itemView.context.getString(R.string.dang_cap_nhat)
        }

        if (obj.variant?.can_add_to_cart == true && obj.shop?.is_online == true) {
            itemView.tvAddToCart.visibility = View.VISIBLE
        } else {
            itemView.tvAddToCart.visibility = View.INVISIBLE
        }

        if (!obj.product?.barcode.isNullOrEmpty()) {
            itemView.tvBarcodeProduct.text = obj.product?.barcode
        } else {
            itemView.tvNameProduct.text = itemView.context.getString(R.string.dang_cap_nhat)
        }

        if (obj.variant != null) {
            itemView.layoutPriceFromMerchant.visibility = View.VISIBLE
            itemView.viewLine1.visibility = View.VISIBLE
            if (obj.variant?.sale_off == true) {
                // neu bang true thi hien thi view gach ngang voi truong price
                itemView.tvPriceSale.text = TextHelper.formatMoneyPhay(obj.variant?.price) + "đ"
                itemView.tvPriceSale.paintFlags = itemView.tvPriceSale.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                itemView.tvPriceProduct.text = TextHelper.formatMoneyPhay(obj.variant?.special_price) + "đ"
            } else {
                // neu bang false thi hien thi view xanh voi truong price
                itemView.tvPriceSale.text = ""
                itemView.tvPriceProduct.text = TextHelper.formatMoneyPhay(obj.variant?.price) + "đ"
            }
        } else {
            itemView.viewLine1.visibility = View.INVISIBLE
            itemView.layoutPriceFromMerchant.visibility = View.GONE
        }

        itemView.ratingBar.rating = obj.product?.rating!!

        if (obj.product?.rating != null) {
            itemView.tvCountRating.rText(R.string.format_1_f, obj.product?.rating!! * 2)
        }

        if (obj.product?.verified == true) {
            itemView.tvVerify.rText(R.string.verified)
            itemView.tvVerify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
        } else {
            itemView.tvVerify.text = ""
            itemView.tvVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        if (!obj.updated_at.isNullOrEmpty()) {
            if (adapterPosition == 0) {
                itemView.tvTime.visibility = View.VISIBLE
                itemView.tvTime.text = TestTimeUtil(obj.updated_at!!).getTime()
            } else {
                val cp1 = TestTimeUtil(listData[adapterPosition - 1]?.updated_at!!).getTime()
                val cp2 = TestTimeUtil(listData[adapterPosition]?.updated_at!!).getTime()

                if (cp1 != cp2) {
                    itemView.tvTime.visibility = View.VISIBLE
                    itemView.tvTime.text = cp2
                } else {
                    itemView.tvTime.visibility = View.GONE
                }
            }
        } else {
            itemView.tvTime.visibility = View.GONE
        }

        if (obj.shop?.id == null || obj.shop?.id == 0L) {
            itemView.layoutShop.visibility = View.GONE
        } else {
            itemView.layoutShop.visibility = View.VISIBLE
        }

        WidgetUtils.loadImageUrl(itemView.avatarShop, obj.shop?.avatar_thumbnails?.thumbnail)

        itemView.tvNameShop.text = if (!obj.shop?.name.isNullOrEmpty()) {
            obj.shop?.name
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        val listService = mutableListOf<ICServiceShopVariant>()
        if (obj.shop?.is_offline == true) {
            listService.add(ICServiceShopVariant(0, R.drawable.ic_offline_shop_variant_18dp, itemView.context rText R.string.mua_tai_cua_hang, "#eb5757", R.drawable.bg_corner_shop_variant_offline))
        }

        if (obj.shop?.verified == true) {
            listService.add(ICServiceShopVariant(1, R.drawable.ic_verified_shop_variant_18px, itemView.context rText R.string.dai_ly_chinh_hang, "#49aa2d", R.drawable.bg_corner_verified_shop_variant))
        }

        if (obj.shop?.is_online == true) {
            listService.add(ICServiceShopVariant(2, R.drawable.ic_online_shop_variant_18px, itemView.context rText R.string.ban_online, "#2d9cdb", R.drawable.bg_corner_online_shop_variant))
        }

        if (!listService.isNullOrEmpty()) {
            initAdapterService()
            adapterService.setListData(listService)
        }

        if (obj.shop?.distance != null) {
            if (obj.shop?.distance?.value.toString().isNotEmpty()) {
                itemView.tvMap.visibility = View.VISIBLE
                km = TextHelper.getTextAfterDot(obj.shop?.distance?.value)

                if (obj.shop?.distance?.value != null) {
                    itemView.tvMap.visibility = View.VISIBLE
                    itemView.tvDistance.rText(R.string.khoang_cach_s_s, km , obj.shop?.distance?.unit)
                } else {
                    itemView.tvDistance.rText(R.string.khoang_cach_s, itemView.context.getString(R.string.dang_cap_nhat))
                    itemView.tvMap.visibility = View.GONE
                }
            } else {
                itemView.tvDistance.rText(R.string.khoang_cach_dang_cap_nhat)
                itemView.tvMap.visibility = View.GONE
            }
        } else {
            itemView.tvDistance.rText(R.string.khoang_cach_dang_cap_nhat)
            itemView.tvMap.visibility = View.GONE
        }
    }

    private fun initAdapterService() {
        adapterService = ServiceShopVariantAdapter(itemView.context)
        itemView.rcvServiceShopVariant.layoutManager = FlexboxLayoutManager(itemView.context, FlexDirection.ROW, FlexWrap.WRAP)
        itemView.rcvServiceShopVariant.adapter = adapterService
    }

    private fun initListener(obj: ICHistory_Product) {
        itemView.avatarShop.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
//                ShopDetailActivity.start(obj.shop?.id, act)
            }
        }

        itemView.layoutShop.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
//                ShopDetailActivity.start(obj.shop?.id, act)
            }
        }

        itemView.tvNameShop.setOnClickListener {
            ICheckApplication.currentActivity()?.let { act ->
//                ShopDetailActivity.start(obj.shop?.id, act)
            }
        }

        itemView.tvAddToCart.setOnClickListener {
            if (SessionManager.isUserLogged) {
                AddToCartHelper.addToCart(itemView.context, obj.variant?.id)
            } else {
                ICheckApplication.currentActivity()?.let { act ->
                    ActivityUtils.startActivity<IckLoginActivity>(act)
                }
            }
        }

        itemView.tvMap.setOnClickListener {
            if (obj.shop?.location != null) {
                if (obj.shop?.location?.lat != null && obj.shop?.location?.lon != null) {
                    val intent = Intent(ICheckApplication.currentActivity(), MapGoogleActivity::class.java)
                    intent.putExtra(Constant.DATA_1, obj.shop?.location?.lat!!)
                    intent.putExtra(Constant.DATA_2, obj.shop?.location?.lon!!)
                    ICheckApplication.currentActivity()?.let { act ->
                        act.startActivity(intent)
                    }
                } else {
                    ToastUtils.showShortError(itemView.context, itemView.context rText R.string.vi_tri_cua_shop_dang_duoc_cap_nhat)
                }
            } else {
                ToastUtils.showShortError(itemView.context, itemView.context rText R.string.vi_tri_cua_shop_dang_duoc_cap_nhat)
            }
        }

        itemView.setOnClickListener {
            if (!obj.product?.barcode.isNullOrEmpty()) {
                ICheckApplication.currentActivity()?.let { act ->
                    IckProductDetailActivity.start(act, obj.product?.barcode!!)
                }
            }
        }

        itemView.tvNameProduct.setOnClickListener {
            if (!obj.product?.barcode.isNullOrEmpty()) {
                ICheckApplication.currentActivity()?.let { act ->
                    IckProductDetailActivity.start(act, obj.product?.barcode!!)
                }
            }
        }
    }

    private fun initRemove(obj: ICHistory_Product) {
        val runnableDelete = Runnable {
            if (SessionManager.isUserLogged) {
                DialogHelper.showConfirm(itemView.context, itemView.context rText R.string.ban_co_muon_xoa_san_pham_nay, true, object : ConfirmDialogListener {
                    override fun onDisagree() {
                    }

                    override fun onAgree() {
                        ICheckApplication.currentActivity()?.let { activity ->
                            if (NetworkHelper.isNotConnected(activity)) {
                                ToastUtils.showLongError(itemView.context, itemView.context rText R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                                return
                            }

                            if (obj.id == null) {
                                ToastUtils.showLongError(activity, itemView.context rText R.string.co_loi_xay_ra_vui_long_thu_lai)
                                return
                            }

                            DialogHelper.showLoading(activity)

                            interactor.deleteItemProduct(url.replace("{id}", "${obj.id}"), object : ICApiListener<ResponseBody> {
                                override fun onSuccess(obj: ResponseBody) {
                                    DialogHelper.closeLoading(activity)
                                    listener.onDeleteItemSuccess(adapterPosition)
                                }

                                override fun onError(error: ICBaseResponse?) {
                                    DialogHelper.closeLoading(activity)
                                    ToastUtils.showLongError(itemView.context, itemView.context rText R.string.co_loi_xay_ra_vui_long_thu_lai)
                                }
                            })
                        }
                    }
                })
            }
            itemView.progressHistory.visibility = View.INVISIBLE
            itemView.imgDelete.visibility = View.INVISIBLE
        }

        val runnableShowView = Runnable {
            isHold = true
            itemView.progressHistory.visibility = View.VISIBLE
            itemView.imgDelete.visibility = View.VISIBLE
            handler.postDelayed(runnableDelete, 2000)
        }

        itemView.setOnTouchListener { v, event ->
            val isLogged = SessionManager.isUserLogged

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isHold) {
                        true
                    } else {
                        isHold = true
                        if (isLogged) {
                            handler.postDelayed(runnableShowView, 500)
                        } else {
                            itemView.progressHistory.visibility = View.INVISIBLE
                            itemView.imgDelete.visibility = View.INVISIBLE
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    isHold = false
                    handler.removeCallbacks(runnableDelete)
                    handler.removeCallbacks(runnableShowView)
                    itemView.progressHistory.visibility = View.INVISIBLE
                    itemView.imgDelete.visibility = View.INVISIBLE
                }
                MotionEvent.ACTION_CANCEL -> {
                    isHold = false
                    handler.removeCallbacks(runnableDelete)
                    handler.removeCallbacks(runnableShowView)
                    itemView.progressHistory.visibility = View.INVISIBLE
                    itemView.imgDelete.visibility = View.INVISIBLE
                }
            }
            false
        }
    }

    companion object {
        fun createShopVariantHistoryHolder(parent: ViewGroup, listData: MutableList<ICHistory_Product?>, listener: IShopVariantListener, url: String): ShopVariantHistoryHolder {
            return ShopVariantHistoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_base_shop_variant_history, parent, false), listData, listener, url)
        }
    }
}
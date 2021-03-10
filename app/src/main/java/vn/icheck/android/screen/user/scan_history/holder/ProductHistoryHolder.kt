package vn.icheck.android.screen.user.scan_history.holder

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_product_history_holder.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.models.history.ICItemHistory
import vn.icheck.android.screen.user.map_scan_history.MapScanHistoryActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.store_sell_history.StoreSellHistoryActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beInvisible
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductHistoryHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_product_history_holder, parent, false)) {

    @SuppressLint("SetTextI18n")
    fun bind(obj: ICItemHistory) {
        if (obj.product != null) {

            initListener(obj)

            if (!obj.product?.image.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlRoundedFitCenter(itemView.imgAvaProduct, obj.product?.image, R.drawable.img_error_ads_product_history, R.drawable.img_error_ads_product_history,SizeHelper.size8)
            } else {
                WidgetUtils.loadImageUrlRoundedFitCenter(itemView.imgAvaProduct, obj.product?.image, R.drawable.img_error_ads_product_history, R.drawable.img_error_ads_product_history,SizeHelper.size8)
            }

            if (obj.product?.verified != null){
                itemView.tvNameProduct.beVisible()
                itemView.tvNameProductUpdating.beInvisible()
                if (obj.product?.verified == true) {
                    if (!obj.product?.name.isNullOrEmpty()) {
                        itemView.tvNameProduct.text = obj.product?.name
                        itemView.tvNameProduct.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_regular)
                        itemView.tvNameProduct.setTextColor(Color.parseColor("#212121"))
                        itemView.tvNameProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
                        itemView.tvNameProduct.compoundDrawablePadding = SizeHelper.size5
                    } else {
                        itemView.tvNameProduct.text = itemView.context.getString(R.string.ten_dang_cap_nhat)
                        itemView.tvNameProduct.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_semi_bold_italic)
                        itemView.tvNameProduct.setTextColor(Color.parseColor("#B4B4B4"))
                        itemView.tvNameProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
                        itemView.tvNameProduct.compoundDrawablePadding = SizeHelper.size5
                    }
                } else {
                    if (!obj.product?.name.isNullOrEmpty()) {
                        itemView.tvNameProduct.text = obj.product?.name
                        itemView.tvNameProduct.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_regular)
                        itemView.tvNameProduct.setTextColor(Color.parseColor("#212121"))
                        itemView.tvNameProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    } else {
                        itemView.tvNameProduct.text = itemView.context.getString(R.string.ten_dang_cap_nhat)
                        itemView.tvNameProduct.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_semi_bold_italic)
                        itemView.tvNameProduct.setTextColor(Color.parseColor("#B4B4B4"))
                        itemView.tvNameProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
            } else {
                itemView.tvNameProduct.beInvisible()
                itemView.tvNameProductUpdating.beVisible()
            }

            if (obj.product?.rating != null && obj.product?.rating != 0f) {
                val total = obj.product?.rating!! * 2
                itemView.tvCountRating.text = String.format("%.1f", total)
            } else {
                itemView.tvCountRating.text = "0"
            }

            if (obj.product?.reviewCount != null){
                if (obj.product?.reviewCount!! < 1000 ){
                    itemView.tvCountReview.text = "(${obj.product?.reviewCount})"
                } else {
                    itemView.tvCountReview.text = "(999+)"
                }
            } else {
                itemView.tvCountReview.text = null
            }

            if (!obj.product?.barcode.isNullOrEmpty()) {
                itemView.tvBarcodeProduct.beVisible()
                itemView.tvBarcodeProduct.text = obj.product?.barcode
            } else {
                itemView.tvBarcodeProduct.beGone()
            }
        }

        if (obj.numShopSell != null && obj.numShopSell != 0) {
            itemView.tvCountShop.text = "Có ${obj.numShopSell} cửa hàng bán sản phẩm này"
            itemView.tvCountShop.visibility = View.VISIBLE
            itemView.layoutShop.visibility = View.VISIBLE
        } else {
            itemView.tvCountShop.visibility = View.INVISIBLE
            itemView.layoutShop.visibility = View.GONE
        }

        if (obj.nearestShop != null) {
            itemView.layoutShop.visibility = View.VISIBLE
            itemView.btnSearchNear.visibility = View.VISIBLE
            if (!obj.nearestShop?.shop?.avatar.isNullOrEmpty()) {
                WidgetUtils.loadImageUrl(itemView.avaShop, obj.nearestShop?.shop?.avatar, R.drawable.ic_error_load_shop_40_px, R.drawable.ic_error_load_shop_40_px)
            } else {
                itemView.avaShop.setImageResource(R.drawable.ic_error_load_shop_40_px)
            }

            itemView.tvNameShop.text = obj.nearestShop?.shop?.name ?: itemView.context.getString(R.string.dang_cap_nhat)

            if (obj.nearestShop?.distance != null) {
                TextHelper.convertMtoKm(obj.nearestShop?.distance!!,itemView.tvDistance)
            } else {
                itemView.tvDistance.text = itemView.context.getString(R.string.dang_cap_nhat)
            }
        } else {
            itemView.layoutShop.visibility = View.GONE
            itemView.btnSearchNear.visibility = View.GONE
        }
    }

    private fun initListener(obj: ICItemHistory) {
        itemView.onDelayClick({
            if (obj.product?.id != null) {
                ICheckApplication.currentActivity()?.let {
                    IckProductDetailActivity.start(it, obj.product?.id!!)
                }
            }
        },1500)

        itemView.tvCountShop.setOnClickListener {
            if (obj.product?.sourceId != null && obj.product?.sourceId != 0L) {
                ICheckApplication.currentActivity()?.let {activity ->
                    ActivityHelper.startActivity<StoreSellHistoryActivity, ICItemHistory>(activity, Constant.DATA_1, obj)
                }
//                val intent = Intent(itemView.context, StoreSellHistoryActivity::class.java)
//                intent.putExtra(Constant.DATA_1, obj)
//                itemView.context.startActivity(intent)
            }
        }

        itemView.btnSearchNear.setOnClickListener {
            if (obj.product?.sourceId != null && obj.product?.sourceId != 0L) {
                val intent = Intent(itemView.context, MapScanHistoryActivity::class.java)
                intent.putExtra(Constant.DATA_2, obj.product?.sourceId!!)
                intent.putExtra(Constant.DATA_3, obj.nearestShop?.shop?.location?.lat)
                intent.putExtra(Constant.DATA_4, obj.nearestShop?.shop?.location?.lon)
                intent.putExtra("avatarShop", obj.nearestShop?.shop?.avatar)
                itemView.context.startActivity(intent)
            }
        }

        itemView.layoutShop.setOnClickListener {
            val intent = Intent(ICheckApplication.currentActivity(), MapScanHistoryActivity::class.java)
            intent.putExtra(Constant.DATA_2, obj.nearestShop?.shop?.id)
            intent.putExtra(Constant.DATA_3, obj.nearestShop?.shop?.location?.lat)
            intent.putExtra(Constant.DATA_4, obj.nearestShop?.shop?.location?.lon)
            intent.putExtra("avatarShop", obj.nearestShop?.shop?.avatar)
            ICheckApplication.currentActivity()?.startActivity(intent)
        }
    }
}
package vn.icheck.android.screen.user.scan_history.holder

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper.onDelayClick
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.LayoutProductHistoryHolderBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.beGone
import vn.icheck.android.ichecklibs.util.beInvisible
import vn.icheck.android.ichecklibs.util.beVisible
import vn.icheck.android.loyalty.helper.ActivityHelper
import vn.icheck.android.network.models.history.ICItemHistory
import vn.icheck.android.screen.user.map_scan_history.MapScanHistoryActivity
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity
import vn.icheck.android.screen.user.store_sell_history.StoreSellHistoryActivity
import vn.icheck.android.util.KeyboardUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ProductHistoryHolder(parent: ViewGroup, val binding: LayoutProductHistoryHolderBinding = LayoutProductHistoryHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(obj: ICItemHistory) {
        if (obj.product != null) {
            initListener(obj)

            if (!obj.product?.image.isNullOrEmpty()) {
                WidgetUtils.loadImageUrlRoundedFitCenter(binding.imgAvaProduct, obj.product?.image, R.drawable.img_error_ads_product_history, R.drawable.img_error_ads_product_history, SizeHelper.size8)
            } else {
                WidgetUtils.loadImageUrlRoundedFitCenter(binding.imgAvaProduct, obj.product?.image, R.drawable.img_error_ads_product_history, R.drawable.img_error_ads_product_history, SizeHelper.size8)
            }

            if (obj.product?.verified != null) {
                binding.tvNameProduct.beVisible()
                binding.tvNameProductUpdating.beInvisible()
                if (obj.product?.verified == true) {
                    if (!obj.product?.name.isNullOrEmpty()) {
                        binding.tvNameProduct.text = obj.product?.name
                        binding.tvNameProduct.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_regular)
                        binding.tvNameProduct.setTextColor(vn.icheck.android.ichecklibs.Constant.getNormalTextColor(itemView.context))
                        binding.tvNameProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
                        binding.tvNameProduct.compoundDrawablePadding = SizeHelper.size5
                    } else {
                        binding.tvNameProduct.text = itemView.context.getString(R.string.ten_dang_cap_nhat)
                        binding.tvNameProduct.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_semi_bold_italic)
                        binding.tvNameProduct.setTextColor(vn.icheck.android.ichecklibs.Constant.getDisableTextColor(itemView.context))
                        binding.tvNameProduct.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home_verify_16dp, 0, 0, 0)
                        binding.tvNameProduct.compoundDrawablePadding = SizeHelper.size5
                    }
                } else {
                    if (!obj.product?.name.isNullOrEmpty()) {
                        binding.tvNameProduct.text = obj.product?.name
                        binding.tvNameProduct.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_regular)
                        binding.tvNameProduct.setTextColor(vn.icheck.android.ichecklibs.Constant.getNormalTextColor(itemView.context))
                        binding.tvNameProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    } else {
                        binding.tvNameProduct.text = itemView.context.getString(R.string.ten_dang_cap_nhat)
                        binding.tvNameProduct.typeface = ResourcesCompat.getFont(itemView.context, R.font.barlow_semi_bold_italic)
                        binding.tvNameProduct.setTextColor(vn.icheck.android.ichecklibs.Constant.getDisableTextColor(itemView.context))
                        binding.tvNameProduct.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
            } else {
                binding.tvNameProduct.beInvisible()
                binding.tvNameProductUpdating.beVisible()
            }

            if (obj.product?.rating != null && obj.product?.rating != 0f) {
                val total = obj.product?.rating!! * 2
                binding.tvCountRating.text = String.format("%.1f", total)
            } else {
                binding.tvCountRating.text = "0"
            }

            binding.tvCountReview.apply {
                text = if (obj.product?.reviewCount != null) {
                    beVisible()
                    if (obj.product?.reviewCount!! < 1000) {
                        "(${obj.product?.reviewCount})"
                    } else {
                        "(999+)"
                    }
                } else {
                    beGone()
                    null
                }
            }

            if (!obj.product?.barcode.isNullOrEmpty()) {
                binding.tvBarcodeProduct.beVisible()
                binding.tvBarcodeProduct.text = obj.product?.barcode?.trim()
            } else {
                binding.tvBarcodeProduct.beGone()
            }
        }

        if (obj.numShopSell != null && obj.numShopSell != 0) {
            binding.tvCountShop.text = "Có ${obj.numShopSell} cửa hàng bán sản phẩm này"
            binding.tvCountShop.visibility = View.VISIBLE
            binding.layoutShop.visibility = View.VISIBLE
        } else {
            binding.tvCountShop.visibility = View.INVISIBLE
            binding.layoutShop.visibility = View.GONE
        }

        binding.btnSearchNear.background=ViewHelper.bgAccentCyanRadiusTop8(binding.btnSearchNear.context)

        if (obj.nearestShop?.shop != null) {
            binding.layoutShop.visibility = View.VISIBLE
            binding.btnSearchNear.visibility = View.VISIBLE
            if (!obj.nearestShop?.shop?.avatar.isNullOrEmpty()) {
                WidgetUtils.loadImageUrl(binding.avaShop, obj.nearestShop?.shop?.avatar, R.drawable.ic_error_load_shop_40_px, R.drawable.ic_error_load_shop_40_px)
            } else {
                binding.avaShop.setImageResource(R.drawable.ic_error_load_shop_40_px)
            }

            binding.tvNameShop.text = obj.nearestShop?.shop?.name
                    ?: itemView.context.getString(R.string.dang_cap_nhat)

            if (obj.nearestShop?.distance != null && obj.nearestShop?.distance != Double.POSITIVE_INFINITY && obj.nearestShop?.distance != Double.NEGATIVE_INFINITY) {
                TextHelper.convertMtoKm(obj.nearestShop?.distance!!.toLong(), binding.tvDistance)
            } else {
                binding.tvDistance.text = itemView.context.getString(R.string.dang_cap_nhat)
            }
        } else {
            binding.layoutShop.visibility = View.GONE
            binding.btnSearchNear.visibility = View.GONE
        }
    }

    private fun initListener(obj: ICItemHistory) {
        itemView.onDelayClick({
            if (obj.product?.id != null) {
                ICheckApplication.currentActivity()?.let { activity ->
                    IckProductDetailActivity.start(activity, obj.product!!.id)
                }
            }
        }, 1500)

        binding.tvCountShop.setOnClickListener {
            if (obj.product?.sourceId != null && obj.product?.sourceId != 0L) {
                ICheckApplication.currentActivity()?.let { activity ->
                    ActivityHelper.startActivity<StoreSellHistoryActivity, ICItemHistory>(activity, Constant.DATA_1, obj)
                }
            }
        }

        binding.btnSearchNear.setOnClickListener {
            if (obj.product?.sourceId != null && obj.product?.sourceId != 0L) {
                ICheckApplication.currentActivity()?.let { activity -> KeyboardUtils.hideSoftInput(activity) }
                val intent = Intent(itemView.context, MapScanHistoryActivity::class.java)
                intent.putExtra(Constant.DATA_2, obj.product?.sourceId!!)
                intent.putExtra(Constant.DATA_3, obj.nearestShop?.shop?.location?.lat)
                intent.putExtra(Constant.DATA_4, obj.nearestShop?.shop?.location?.lon)
                intent.putExtra("avatarShop", obj.nearestShop?.shop?.avatar)
                itemView.context.startActivity(intent)
            }
        }

        binding.layoutShop.setOnClickListener {
//            val intent = Intent(ICheckApplication.currentActivity(), MapScanHistoryActivity::class.java)
//            intent.putExtra(Constant.DATA_2, obj.nearestShop?.shop?.id)
//            intent.putExtra(Constant.DATA_3, obj.nearestShop?.shop?.location?.lat)
//            intent.putExtra(Constant.DATA_4, obj.nearestShop?.shop?.location?.lon)
//            intent.putExtra("avatarShop", obj.nearestShop?.shop?.avatar)
//            ICheckApplication.currentActivity()?.startActivity(intent)

            binding.btnSearchNear.performClick()
        }
    }
}
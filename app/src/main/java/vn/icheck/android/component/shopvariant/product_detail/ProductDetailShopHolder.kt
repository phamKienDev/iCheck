package vn.icheck.android.component.shopvariant.product_detail

import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.component.view.text_view.TextViewBarlowSemiBold
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.list_shop_variant.ListShopVariantActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class ProductDetailShopHolder(parent: View) : BaseHolder(ViewHelper.createProductShopVariant(parent.context)) {

    fun bind(obj: ShopProductModel) {
        (itemView as ViewGroup).run {
            for (i in (childCount - 1) downTo 1) {
                removeViewAt(i)
            }

            (getChildAt(0) as TextViewBarlowSemiBold).apply {
                text = if (obj.listShop.size > 0) {
                    context.getString(R.string.diem_ban_gan_day_xxx, obj.listShop.size)
                } else {
                    context.getString(R.string.diem_ban_gan_day)
                }
            }

            addView(ProductDetailShopVariantComponent(context).also {
                it.bind(obj.listShop[0])
            })

            if (obj.listShop.size > 1) {
                addView(ProductDetailShopVariantComponent(context).also {
                    it.bind(obj.listShop[1])
                })
            }

            if (obj.listShop.size > 2)
                addView(LinearLayout(context).also { layoutMoreShop ->
                    layoutMoreShop.layoutParams = ViewHelper.createLayoutParams32Dp(SizeHelper.size28, SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, SizeHelper.size10)
                    layoutMoreShop.orientation = LinearLayout.VERTICAL
                    layoutMoreShop.gravity = Gravity.CENTER
                    layoutMoreShop.background = ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_no_solid)

                    layoutMoreShop.addView(TextViewBarlowSemiBold(context).also { text ->
                        text.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        text.setTextColor(ContextCompat.getColor(context, R.color.lightBlue))
                        text.gravity = Gravity.CENTER
                        text.setText(R.string.xem_tat_ca_diem_ban)
                        text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_blue_24px, 0)
                    })

                    layoutMoreShop.setOnClickListener {
                        if (obj.listShop.size >= 10) {
                            ICheckApplication.currentActivity()?.let { activity ->
                                ActivityUtils.startActivity<ListShopVariantActivity>(activity, Constant.DATA_1, JsonHelper.toJson(obj.listShop))
                            }
                        } else {
                            if (obj.listShop.size > 2) {
                                for (i in 2 until obj.listShop.size) {
                                    addView(ProductDetailShopVariantComponent(context).also {
                                        it.bind(obj.listShop[i])
                                    })
                                }
                            }
                            layoutMoreShop.setGone()
                        }
                    }
                })
        }
    }
}
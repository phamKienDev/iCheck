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
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.network.models.ICShopVariantV2
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.list_shop_variant.ListShopVariantActivity
import vn.icheck.android.ui.view.TextBarlowSemiBold
import vn.icheck.android.util.kotlin.ActivityUtils

class ListShopHolder(parent: View) : BaseHolder(ViewHelper.createProductShopVariant(parent.context)) {

    fun bind(obj: MutableList<ICShopVariantV2>) {
        (itemView as ViewGroup).run {
            for (i in (childCount - 1) downTo 1) {
                removeViewAt(i)
            }

            (getChildAt(0) as TextBarlowSemiBold).apply {
                text = if (obj.size > 0) {
                    context.getString(R.string.diem_ban_gan_day_xxx, obj.size)
                } else {
                    context.getString(R.string.diem_ban_gan_day)
                }
            }

            addView(ProductDetailShopVariantComponent(context).also {
                it.bind(obj[0])
            })

            if (obj.size > 1) {
                addView(ProductDetailShopVariantComponent(context).also {
                    it.bind(obj[1])
                })
            }

            if (obj.size > 2)
                addView(LinearLayout(context).also { layoutMoreShop ->
                    layoutMoreShop.layoutParams = ViewHelper.createLayoutParams32Dp(SizeHelper.size28, SizeHelper.size12, SizeHelper.size10, SizeHelper.size12, SizeHelper.size10)
                    layoutMoreShop.orientation = LinearLayout.VERTICAL
                    layoutMoreShop.gravity = Gravity.CENTER
                    layoutMoreShop.background = ContextCompat.getDrawable(context, R.drawable.bg_corners_4_light_blue_no_solid)

                    layoutMoreShop.addView(TextBarlowSemiBold(context).also { text ->
                        text.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        text.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        text.gravity = Gravity.CENTER
                        text.setText(R.string.xem_tat_ca_diem_ban)
                        text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_blue_24px, 0)
                    })

                    layoutMoreShop.setOnClickListener {
                        if (obj.size >= 10) {
                            ICheckApplication.currentActivity()?.let { activity ->
                                ActivityUtils.startActivity<ListShopVariantActivity>(activity, Constant.DATA_1, JsonHelper.toJson(obj))
                            }
                        } else {
                            if (obj.size > 2) {
                                for (i in 2 until obj.size) {
                                    addView(ProductDetailShopVariantComponent(context).also {
                                        it.bind(obj[i])
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
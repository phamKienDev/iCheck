package vn.icheck.android.screen.user.scan_history.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_shop_history_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICHistory_Shop
import vn.icheck.android.util.kotlin.WidgetUtils

class ShopHistoryHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_shop_history_holder, parent, false)) {
    private var expand = false // true: đã mở rộng
    private var km: String? = null

    fun bind(item: ICHistory_Shop?) {
        initListener(item)

        //SHOP
//        if (item.distance != null) {
//            if (item.distance?.value.toString().isNotEmpty()) {
//                km = TextHelper.getTextAfterDot(item.distance?.value)
//
//                if (item.distance?.value != null) {
//                    itemView.tvDistance.text = "Khoảng cách: " + km + item.distance?.unit
//                } else {
//                    itemView.tvDistance.text = itemView.context.getString(R.string.dang_cap_nhat)
//                }
//            } else {
//                itemView.tvDistance.text = itemView.context.getString(R.string.dang_cap_nhat)
//            }
//        } else {
//            itemView.tvDistance.text = itemView.context.getString(R.string.dang_cap_nhat)
//        }
//
//
//        itemView.tvNameShop.text = item.name
//
//        val image = item.avatar_thumbnails?.thumbnail
//        if (!image.isNullOrEmpty()) {
//            WidgetUtils.loadImageUrlRounded4(itemView.avatarShop, image)
//        } else {
//            itemView.avatarShop.setImageResource(R.drawable.img_default_shop_logo)
//        }
//
//        itemView.imgShop.visibility = if (item.is_offline == true) {
//            View.VISIBLE
//        } else {
//            View.GONE
//        }
//
//        itemView.imgVerified.visibility = if (item.verified == true) {
//            View.VISIBLE
//        } else {
//            View.GONE
//        }
//
//        itemView.imgGlobal.visibility = if (item.is_online == true) {
//            View.VISIBLE
//        } else {
//            View.GONE
//        }

        itemView.tvBuyNow.background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(itemView.context)

        //PRODUCT

        if (expand) {
            itemView.tvAllProduct.text = itemView.context.getString(R.string.xem_tat_ca_san_pham)
            itemView.tvCollapse.text = itemView.context.getString(R.string.thu_gon)
            itemView.tvCollapse.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_collapse_history_24dp, 0)
            itemView.viewLine2.visibility = View.VISIBLE
        } else {
            itemView.tvAllProduct.setText(R.string.x_san_pham_co_san)
            itemView.tvCollapse.text = itemView.context.getString(R.string.xem_them)
            itemView.tvCollapse.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_extend_history_24dp, 0)
            itemView.viewLine2.visibility = View.GONE
        }

        itemView.tvCollapse.setOnClickListener {
            if (expand) {
                itemView.tvAllProduct.setText(R.string.x_san_pham_co_san)
                itemView.tvCollapse.text = itemView.context.getString(R.string.xem_them)
                itemView.tvCollapse.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_extend_history_24dp, 0)
                itemView.viewLine2.visibility = View.GONE
                expand = false
                WidgetUtils.changeViewHeight(itemView.containerProduct, itemView.containerProduct.layoutParams.height, 0, 300)
            } else {
                expand = true
                itemView.viewLine2.visibility = View.VISIBLE
                itemView.tvAllProduct.text = itemView.context.getString(R.string.xem_tat_ca_san_pham)
                itemView.tvCollapse.text = itemView.context.getString(R.string.thu_gon)
                itemView.tvCollapse.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_collapse_history_24dp, 0)
                WidgetUtils.changeViewHeight(itemView.containerProduct, 0, SizeHelper.dpToPx(150), 300)

            }
        }
    }

    private fun initListener(item: ICHistory_Shop?) {
        itemView.imgVerified.setOnClickListener {
            itemView.imgVerified2.visibility = if (itemView.imgVerified2.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        itemView.imgGlobal.setOnClickListener {
            itemView.imgGlobal2.visibility = if (itemView.imgGlobal2.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }
}



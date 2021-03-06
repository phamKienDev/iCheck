package vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.holder

import android.content.Intent
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_product_redeem_point.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKBoxGifts
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.detail_gift_point.DetailGiftLoyaltyActivity
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.HomeRedeemPointActivity

class ProductHomeRedeemPointLoyaltyHolder(parent: ViewGroup) : BaseViewHolder<ICKBoxGifts>(R.layout.item_product_redeem_point, parent) {
    override fun bind(obj: ICKBoxGifts) {
        WidgetHelper.loadImageUrl(itemView.imgProduct, obj.gift?.image?.medium)

        itemView.tvPoint.text = TextHelper.formatMoneyPhay(obj.points)

        checkNullOrEmpty(itemView.tvNameProduct, obj.gift?.name)

        /**
        ICOIN: "ICOIN",
        PRODUCT: "PRODUCT",
        CONGRATULATION: "CONGRATULATION",
        PHONE_CARD: "PHONE_CARD",
        RECEIVE_STORE: "RECEIVE_STORE",
        VOUCHER -> "Voucher"
         */
        itemView.tvCheck.text = if (!obj.gift?.type.isNullOrEmpty()) {
            when (obj.gift?.type) {
                "ICOIN" -> itemView.context.getString(R.string.xu_icheck)
                "PHONE_CARD" -> itemView.context.getString(R.string.the_cao_dien_thoai)
                "RECEIVE_STORE" -> itemView.context.getString(R.string.nhan_tai_cua_hang)
                "PRODUCT" -> itemView.context.getString(R.string.giao_tan_noi)
                "VOUCHER" -> itemView.context.getString(R.string.voucher)
                else -> itemView.context.getString(R.string.qua_tinh_than)
            }
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        itemView.setOnClickListener {
            SharedLoyaltyHelper(itemView.context).putLong(ConstantsLoyalty.COUNT_GIFT, obj.quota ?: 0)
            DetailGiftLoyaltyActivity.obj = obj
            itemView.context.startActivity(Intent(itemView.context, DetailGiftLoyaltyActivity::class.java).apply {
//                putExtra(ConstantsLoyalty.DATA_1, obj)
                putExtra(ConstantsLoyalty.DATA_2, HomeRedeemPointActivity.banner)
                putExtra(ConstantsLoyalty.DATA_3, HomeRedeemPointActivity.campaignID)
            })
        }
    }
}
package vn.icheck.android.loyalty.screen.gift_detail_voucher

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_detail.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKScanVoucher

internal class GiftVoucherStaffAdapter : RecyclerViewCustomAdapter<ICKScanVoucher>() {

    fun setData(obj: ICKScanVoucher) {
        listData.clear()

        listData.add(obj)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return ICKViewType.ITEM_TYPE
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {
            holder.bind(listData[position])
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKScanVoucher>(R.layout.item_gift_detail, parent) {

        @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
        override fun bind(obj: ICKScanVoucher) {
            WidgetHelper.loadImageUrl(itemView.imgBanner, obj.businessLoyalty?.businessLoyalty?.banner?.original)
            WidgetHelper.loadImageUrlRounded6(itemView.imgProduct, obj.gift?.image?.original)

            itemView.tvProduct.text = if (!obj.gift?.name.isNullOrEmpty()) {
                obj.gift?.name
            } else {
                default
            }

            itemView.tvStatus.apply {
                setVisible()

                text = "Đã nhận quà"
                setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
            }

            itemView.tvRedemptionPoints.text = TextHelper.formatMoneyPhay(obj.businessLoyalty?.point_exchange)

            if (obj.id != null) {
                itemView.layoutCodeGift.setVisible()

                itemView.tvCodeGift.text = obj.id.toString()
            } else {
                itemView.layoutCodeGift.setGone()
            }

            itemView.tvVanChuyen.text = "Voucher"

            itemView.btnDoiQua.apply {
                if (obj.gift?.type == "VOUCHER") {
                    itemView.layoutCodeGift.setGone()
                    setVisible()
                    when (obj.voucher?.can_mark_use) {
                        true -> {
                            text = "Đánh dầu đã dùng"

                            setOnClickListener {
                                showCustomErrorToast(itemView.context, "Chưa có sự kiện")
                            }
                        }
                        else -> {
                            setGone()
                        }
                    }
                } else {
                    setGone()
                }
            }

            itemView.webView.settings.javaScriptEnabled = true
            itemView.webView.loadDataWithBaseURL(null, obj.gift?.description
                    ?: "", "text/html; charset=utf-8", "UTF-8", null)

            WidgetHelper.loadImageUrl(itemView.imgAvatar, obj.owner?.logo?.medium)

            itemView.tvNameShop.text = if (!obj.owner?.name.isNullOrEmpty()) {
                obj.owner?.name
            } else {
                default
            }
        }
    }
}
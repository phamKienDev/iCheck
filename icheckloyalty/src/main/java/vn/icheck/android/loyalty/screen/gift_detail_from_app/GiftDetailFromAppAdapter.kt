package vn.icheck.android.loyalty.screen.gift_detail_from_app

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_detail_from_app.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.helper.TimeHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKGift

internal class GiftDetailFromAppAdapter : RecyclerViewCustomAdapter<ICKGift>() {

    fun setData(obj: ICKGift) {
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
        super.onBindViewHolder(holder, position)

        if (holder is ViewHolder) {
            holder.bind(listData[position])
        }
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKGift>(R.layout.item_gift_detail_from_app, parent) {

        @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
        override fun bind(obj: ICKGift) {
            WidgetHelper.loadImageUrl(itemView.imgGift, obj.image?.original)

            WidgetHelper.loadImageUrl(itemView.imgAvatar, obj.shop_image)

            checkNullOrEmpty(itemView.tvNameShop, obj.shop_name)
            checkNullOrEmpty(itemView.tvNameGift, obj.name)

            itemView.tvCategoryGift.text = when (obj.rewardType) {
                "spirit" -> {
                    itemView.tvCode.setVisible()
                    "Tinh thần"
                }
                "PRODUCT_IN_SHOP" -> {
                    itemView.tvCode.setVisible()
                    "Quà nhận tại cửa hàng"
                }
                "CARD" -> {
                    itemView.tvCode.setVisible()
                    "Quà thẻ cào"
                }
                "product" -> {
                    itemView.tvCode.setVisible()
                    "Hiện vật"
                }
                "VOUCHER" -> {
                    itemView.layoutMaDuThuong.setGone()
                    itemView.tvCode.setGone()
                    "Voucher"
                }
                else -> {
                    ""
                }
            }

            if (obj.rewardType?.contains("VOUCHER") == true) {
                itemView.layoutStatusGift.setGone()

                if (obj.voucher != null) {

                    if (obj.voucher.checked_condition?.status == false) {
                        itemView.tvStatus.text = "Hết hạn sử dụng"
                    } else {
                        itemView.tvStatus.text = "Có thể sử dụng"
                    }
                } else {

                    when (obj.state) {
                        1 -> {
                            itemView.tvStatus.text = "Chờ xác nhận"
                        }
                        3 -> {
                            itemView.tvStatus.text = "Từ chối"
                        }
                    }
                }
            } else {
                when (obj.state) {
                    1 -> {
                        itemView.layoutStatusGift.setGone()
                        itemView.tvStatus.text = "Chưa nhận"
                    }
                    2 -> {
                        itemView.tvStatus.text = "Đã nhận quà"

                        itemView.layoutStatusGift.setVisible()
                        itemView.tvStatusGift.run {
                            setTextColor(getColor(R.color.green2))
                            text = "Đã nhận quà"
                        }
                    }
                    3 -> {
                        itemView.layoutStatusGift.setVisible()
                        itemView.tvStatusGift.run {
                            setTextColor(getColor(R.color.errorColor))
                            text = "Bạn đã từ chối nhận quà này"
                        }
                        itemView.tvStatus.text = "Bạn đã từ chối nhận quà này"
                    }
                    4 -> {
                        itemView.layoutStatusGift.setVisible()
                        itemView.tvStatusGift.run {
                            setTextColor(getColor(R.color.green2))
                            text = "Bạn đã xác nhận ship quà này"
                        }
                        itemView.tvStatus.text = "Chờ giao"
                    }
                    else -> {
                        itemView.layoutStatusGift.setGone()
                        itemView.tvStatus.text = default
                    }
                }
            }

            itemView.tvTimeGift.text = TimeHelper.convertDateTimeSvToTimeDateVn(obj.expired_at)

            if (!obj.desc.isNullOrEmpty()) {
                itemView.webView.settings.javaScriptEnabled = true
                itemView.webView.loadData(obj.desc, "text/html; charset=utf-8", "UTF-8")
            }

            itemView.tvCode.text = obj.id.toString()
        }
    }
}
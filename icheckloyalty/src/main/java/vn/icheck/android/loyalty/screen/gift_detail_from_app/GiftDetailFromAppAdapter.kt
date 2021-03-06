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

            itemView.tvCategoryGift.apply {
                text = when (obj.rewardType) {
                    "spirit" -> {
                        itemView.tvCode.setVisible()
                        context.getString(R.string.tinh_than)
                    }
                    "PRODUCT_IN_SHOP" -> {
                        itemView.tvCode.setVisible()
                        context.getString(R.string.qua_nhan_tai_cua_hang)
                    }
                    "CARD" -> {
                        itemView.tvCode.setVisible()
                        context.getString(R.string.qua_the_cao)
                    }
                    "product" -> {
                        itemView.tvCode.setVisible()
                        context.getString(R.string.hien_vat)
                    }
                    "VOUCHER" -> {
                        itemView.layoutMaDuThuong.setGone()
                        itemView.tvCode.setGone()
                        context.getString(R.string.voucher)
                    }
                    else -> {
                        ""
                    }
                }
            }

            if (obj.rewardType?.contains("VOUCHER") == true) {
                itemView.layoutStatusGift.setGone()

                if (obj.voucher != null) {
                    itemView.tvTitleDate.setText(R.string.han_su_dung)

                    if (obj.voucher.checked_condition?.status == false) {

                        when(obj.voucher.checked_condition?.code){
                            "START_TIME_CAN_USE" -> {
                                itemView.tvTitleDate.setText(R.string.co_hieu_luc_tu)

                                itemView.tvTimeGift.text = TimeHelper.convertDateTimeSvToDateVn(obj.voucher.start_at)

                                itemView.tvStatus.setText(R.string.chua_co_hieu_luc)
                            }
                            "MAX_NUM_OF_USED_VOUCHER", "MAX_NUM_OF_USED_CUSTOMER" -> {
                                itemView.layoutDate.setGone()

                                itemView.tvStatus.setText(R.string.het_luot_su_dung)
                            }
                            "BUSINESS_LOCKED_VOUCHER", "ADMIN_LOCKED_VOUCHER" -> {
                                itemView.tvTimeGift.text = ""

                                itemView.tvStatus.setText(R.string.da_bi_khoa)
                            }
                            else -> {
                                itemView.tvTimeGift.text = ""

                                itemView.tvStatus.setText(R.string.het_han_su_dung)
                            }
                        }

                    } else {

                        itemView.tvTimeGift.text = TimeHelper.timeGiftVoucher(obj.voucher)

                        itemView.tvStatus.apply {
                            text = if (itemView.tvTimeGift.text.toString() == context.getString(R.string.con_lai)) {

                                itemView.tvTimeGift.text = ""
                                context.getString(R.string.het_han_su_dung)
                            } else {
                                context.getString(R.string.co_the_su_dung)
                            }
                        }
                    }
                } else {

                    when (obj.state) {
                        1 -> {
                            itemView.tvStatus.setText(R.string.cho_xac_nhan)
                        }
                        3 -> {
                            itemView.tvStatus.setText(R.string.tu_choi)
                        }
                    }

                    itemView.tvTimeGift.text = TimeHelper.convertDateTimeSvToTimeDateVn(obj.expired_at)
                }
            } else {
                when (obj.state) {
                    1 -> {
                        itemView.layoutStatusGift.setGone()
                        itemView.tvStatus.setText(R.string.chua_nhan)
                    }
                    2 -> {
                        itemView.tvStatus.setText(R.string.da_nhan_qua)

                        itemView.layoutStatusGift.setVisible()
                        itemView.tvStatusGift.run {
                            setTextColor(getColor(R.color.green2))
                            text = context.getString(R.string.da_nhan_qua)
                        }
                    }
                    3 -> {
                        itemView.layoutStatusGift.setVisible()
                        itemView.tvStatusGift.run {
                            setTextColor(getColor(R.color.errorColor))
                            text = context.getString(R.string.ban_da_tu_choi_nhan_qua_nay)
                        }
                        itemView.tvStatus.setText(R.string.ban_da_tu_choi_nhan_qua_nay)
                    }
                    4 -> {
                        itemView.layoutStatusGift.setVisible()
                        itemView.tvStatusGift.run {
                            setTextColor(getColor(R.color.green2))
                            text = context.getString(R.string.ban_da_xac_nhan_ship_qua_nay)
                        }
                        itemView.tvStatus.setText(R.string.cho_giao)
                    }
                    else -> {
                        itemView.layoutStatusGift.setGone()
                        itemView.tvStatus.text = default
                    }
                }

                itemView.tvTimeGift.text = TimeHelper.convertDateTimeSvToTimeDateVn(obj.expired_at)
            }

            if (!obj.desc.isNullOrEmpty()) {
                itemView.webViewUrl.settings.javaScriptEnabled = true
                itemView.webViewUrl.loadData(obj.desc, "text/html; charset=utf-8", "UTF-8")
            }

            itemView.tvCode.text = obj.id.toString()
        }
    }
}
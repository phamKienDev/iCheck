package vn.icheck.android.loyalty.screen.loyalty_customers.giftdetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_detail.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.dialog.listener.IClickButtonDialog
import vn.icheck.android.loyalty.dialog.listener.IDismissDialog
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.TimeHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKNone
import vn.icheck.android.loyalty.model.ICKRedemptionHistory
import vn.icheck.android.loyalty.screen.loyalty_customers.campaign_of_business.CampaignOfBusinessActivity
import vn.icheck.android.loyalty.screen.voucher.VoucherLoyaltyActivity

internal class GiftDetailAdapter(val type: Int = 0) : RecyclerViewCustomAdapter<Any>() {

    fun setData(obj: ICKRedemptionHistory) {
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
            if (listData[position] is ICKRedemptionHistory) {
                if (type == 0) {
                    holder.bindDetailMyGift(listData[position] as ICKRedemptionHistory)
                } else {
                    holder.bindDetailGiftStore(listData[position] as ICKRedemptionHistory)
                }
            }
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_gift_detail, parent, false)) {
        val default = itemView.context.getString(R.string.dang_cap_nhat)

        @SuppressLint("SetJavaScriptEnabled")
        fun bindDetailMyGift(obj: ICKRedemptionHistory) {
            WidgetHelper.loadImageUrl(itemView.imgBanner, obj.businessLoyalty?.businessLoyalty?.banner?.original)
            WidgetHelper.loadImageUrlRounded6(itemView.imgProduct, obj.gift?.image?.original)

            itemView.tvProduct.text = if (!obj.gift?.name.isNullOrEmpty()) {
                obj.gift?.name
            } else {
                default
            }

            if (obj.gift?.type == "VOUCHER") {
                itemView.tvStatus.setVisible()
                itemView.layoutDate.setVisible()

                if (obj.voucher?.checked_condition?.status == false) {
                    itemView.tvTitleDate.text = "Hạn sử dụng"

                    when (obj.voucher?.checked_condition?.code) {
                        "START_TIME_CAN_USE" -> {
                            itemView.tvTitleDate.text = "Có hiệu lực từ"

                            itemView.tvDateTime.text = TimeHelper.convertDateTimeSvToDateVn(obj.voucher?.start_at)

                            itemView.tvStatus.apply {
                                text = "Chưa có hiệu lực"
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                            }
                        }
                        "MAX_NUM_OF_USED_VOUCHER", "MAX_NUM_OF_USED_CUSTOMER" -> {
                            itemView.layoutDate.setGone()

                            itemView.tvStatus.apply {
                                text = "Hết lượt sử dụng"
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                                setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                            }
                        }
                        "BUSINESS_LOCKED_VOUCHER", "ADMIN_LOCKED_VOUCHER" -> {
                            itemView.tvDateTime.text = ""

                            itemView.tvStatus.apply {
                                text = "Đã bị khóa"
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                                setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                            }
                        }
                        else -> {

                            itemView.tvDateTime.text = ""

                            itemView.tvStatus.apply {
                                text = "Hết hạn sử dụng"
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                                setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                            }
                        }
                    }

                } else {
                    itemView.tvTitleDate.text = "Hạn sử dụng"

                    itemView.tvDateTime.text = obj.voucher?.let { TimeHelper.timeGiftVoucher(it) }

                    itemView.tvStatus.apply {

                        if (itemView.tvDateTime.text.toString() == "Còn lại ") {

                            itemView.tvDateTime.text = ""
                            text = "Hết hạn sử dụng"
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                            setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                        } else {

                            text = "Có thể sử dụng"
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                            setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
                        }
                    }
                }
            } else {
                itemView.tvStatus.apply {
                    setVisible()
                    when (obj.status) {
                        "new" -> {
                            text = "Chờ xác nhận"
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                            setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                        }
                        "waiting_receive_gift" -> {
                            text = "Chờ giao"
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                            setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                        }
                        "received_gift" -> {
                            text = "Đã nhận quà"
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                            setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
                        }
                        "refused_gift" -> {
                            text = "Từ chối"
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                            setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                        }
                        else -> {
                            setGone()
                        }
                    }
                }

                if (obj.id != null) {
                    itemView.layoutCodeGift.setVisible()

                    itemView.tvCodeGift.text = obj.id.toString()
                } else {
                    itemView.layoutCodeGift.setGone()
                }
            }

            itemView.tvRedemptionPoints.text = TextHelper.formatMoneyPhay(obj.businessLoyalty?.point_exchange)

            itemView.tvVanChuyen.text = when (obj.gift?.type) {
                "ICOIN" -> {
                    "Quà Xu"
                }
                "PHONE_CARD" -> {
                    "Quà thẻ cào"
                }
                "RECEIVE_STORE" -> {
                    "Quà nhận tại cửa hàng"
                }
                "PRODUCT" -> {
                    "Quà hiện vật"
                }
                "VOUCHER" -> {
                    "Voucher"
                }
                else -> {
                    "Quà tinh thần"
                }
            }
            itemView.btnDoiQua.apply {
                if (obj.gift?.type == "VOUCHER") {
                    itemView.layoutCodeGift.setGone()
                    setVisible()
                    when {
                        obj.voucher?.can_use == true -> {
                            text = "Dùng ngay"

                            setOnClickListener {
                                itemView.context.startActivity(Intent(itemView.context, VoucherLoyaltyActivity::class.java).apply {
                                    putExtra(ConstantsLoyalty.DATA_1, obj.voucher?.code)
                                    putExtra(ConstantsLoyalty.DATA_2, itemView.tvDateTime.text.toString().trim())
                                    putExtra(ConstantsLoyalty.DATA_3, obj.owner?.logo?.thumbnail)
                                })
                            }
                        }
                        obj.voucher?.can_mark_use == true -> {
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

        @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
        fun bindDetailGiftStore(obj: ICKRedemptionHistory) {

            WidgetHelper.loadImageUrl(itemView.imgBanner, obj.network?.banner?.medium)
            WidgetHelper.loadImageUrlRounded6(itemView.imgProduct, obj.loyalty_gift?.gift?.image?.medium)

            itemView.layoutCountGift.setVisible()

            itemView.tvCountGift.text = if (obj.loyalty_gift?.quantity_remain != null) {
                "${TextHelper.formatMoneyPhay(obj.loyalty_gift?.quantity_remain)} Quà"
            } else {
                "0 Quà"
            }

            itemView.tvProduct.text = if (!obj.loyalty_gift?.gift?.name.isNullOrEmpty()) {
                obj.loyalty_gift?.gift?.name
            } else {
                default
            }

            itemView.tvRedemptionPoints.text = TextHelper.formatMoneyPhay(obj.loyalty_gift?.point_exchange)

            itemView.btnDoiQua.setVisible()

            itemView.tvVanChuyen.text = when (obj.loyalty_gift?.gift?.type) {
                "ICOIN" -> {
                    setOnClickButton(obj)
                    "Quà Xu"
                }
                "PHONE_CARD" -> {
                    setOnClickButton(obj)
                    "Quà thẻ cào"
                }
                "RECEIVE_STORE" -> {
                    itemView.btnDoiQua.text = "Hướng dẫn đổi quà"
                    itemView.btnDoiQua.setOnClickListener {
                        DialogHelperGame.dialogTutorialLoyalty(itemView.context, R.drawable.bg_gradient_button_blue)
                    }
                    "Quà nhận tại cửa hàng"
                }
                "PRODUCT" -> {
                    itemView.layoutPhiVanChuyen.setVisible()
                    setOnClickButton(obj)
                    "Quà hiện vật"
                }
                "VOUCHER" -> {
                    setOnClickButton(obj)
                    "Quà Voucher"
                }
                else -> {
                    itemView.btnDoiQua.setGone()
                    "Quà tinh thần"
                }
            }

            itemView.webView.settings.javaScriptEnabled = true
            itemView.webView.loadDataWithBaseURL(null, obj.loyalty_gift?.gift?.description
                    ?: "", "text/html; charset=utf-8", "UTF-8", null)

            WidgetHelper.loadImageUrl(itemView.imgAvatar, obj.business?.logo?.medium)

            itemView.tvNameShop.text = if (!obj.business?.name.isNullOrEmpty()) {
                obj.business?.name
            } else {
                default
            }

            if (obj.customer != null) {
                itemView.layoutPointMe.setVisible()

                itemView.tvPointMe.text = TextHelper.formatMoneyPhay(obj.customer?.point)
                itemView.tvCategoryPoint.text = if (!obj.network?.point_name.isNullOrEmpty()) {
                    obj.network?.point_name
                } else {
                    ""
                }
            } else {
                itemView.layoutPointMe.setGone()
            }
        }

        private fun setOnClickButton(obj: ICKRedemptionHistory) {
            itemView.btnDoiQua.setOnClickListener {
                itemView.btnDoiQua.isEnabled = false

                if (obj.customer?.point ?: 0 > obj.loyalty_gift?.point_exchange ?: 0) {
                    DialogHelperGame.dialogConfirmExchangeGifts(itemView.context, obj.loyalty_gift?.gift?.image?.original, obj.loyalty_gift?.gift?.name, obj.loyalty_gift?.point_exchange, obj.loyalty_gift?.gift?.type, (obj.loyalty_gift?.quantity_remain
                            ?: 0).toInt(), R.drawable.bg_gradient_button_blue, obj.loyalty_gift?.id
                            ?: -1)
                } else {
                    DialogHelperGame.dialogScanLoyaltyError(itemView.context,
                            R.drawable.ic_error_scan_game, "Bạn không đủ điểm đổi quà!",
                            "Tích cực tham gia các chương trình của\nnhãn hàng để nhận điểm Thành viên nhé",
                            null, "Tích điểm ngay", false, R.drawable.bg_button_not_enough_point_blue_v2, R.color.blueVip,
                            object : IClickButtonDialog<ICKNone> {
                                override fun onClickButtonData(data: ICKNone?) {
                                    itemView.context.startActivity(Intent(itemView.context, CampaignOfBusinessActivity::class.java).apply {
                                        putExtra(ConstantsLoyalty.DATA_1, obj.business?.id)
                                    })
                                }
                            }, object : IDismissDialog {
                        override fun onDismiss() {

                        }
                    })
                }

                Handler().postDelayed({
                    itemView.btnDoiQua.isEnabled = true
                }, 2000)
            }
        }
    }
}
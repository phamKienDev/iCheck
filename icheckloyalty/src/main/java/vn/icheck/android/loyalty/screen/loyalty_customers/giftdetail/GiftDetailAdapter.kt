package vn.icheck.android.loyalty.screen.loyalty_customers.giftdetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_detail.view.*
import vn.icheck.android.ichecklibs.util.getString
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
                    itemView.tvTitleDate.setText(R.string.han_su_dung)

                    when (obj.voucher?.checked_condition?.code) {
                        "START_TIME_CAN_USE" -> {
                            itemView.tvTitleDate.setText(R.string.co_hieu_luc_tu)

                            itemView.tvDateTime.text = TimeHelper.convertDateTimeSvToDateVn(obj.voucher?.start_at)

                            itemView.tvStatus.apply {
                                text = context.getString(R.string.co_hieu_luc_tu)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                            }
                        }
                        "MAX_NUM_OF_USED_VOUCHER", "MAX_NUM_OF_USED_CUSTOMER" -> {
                            itemView.layoutDate.setGone()

                            itemView.tvStatus.apply {
                                text = context.getString(R.string.het_luot_su_dung)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                                setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                            }
                        }
                        "BUSINESS_LOCKED_VOUCHER", "ADMIN_LOCKED_VOUCHER" -> {
                            itemView.tvDateTime.text = ""

                            itemView.tvStatus.apply {
                                text = context.getString(R.string.da_bi_khoa)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                                setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                            }
                        }
                        else -> {

                            itemView.tvDateTime.text = ""

                            itemView.tvStatus.apply {
                                text = context.getString(R.string.het_han_su_dung)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                                setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                            }
                        }
                    }

                } else {
                    itemView.tvTitleDate.setText(R.string.han_su_dung)

                    itemView.tvDateTime.text = obj.voucher?.let { TimeHelper.timeGiftVoucher(it) }

                    itemView.tvStatus.apply {

                        if (itemView.tvDateTime.text.toString() == context.getString(R.string.con_lai_)) {

                            itemView.tvDateTime.text = ""
                            text = context.getString(R.string.het_han_su_dung)
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                            setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                        } else {

                            text = context.getString(R.string.co_the_su_dung)
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
                            text = context.getString(R.string.cho_xac_nhan)
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                            setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                        }
                        "waiting_receive_gift" -> {
                            text = context.getString(R.string.cho_giao)
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                            setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                        }
                        "received_gift" -> {
                            text = context.getString(R.string.da_nhan_qua)
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                            setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
                        }
                        "refused_gift" -> {
                            text = context.getString(R.string.tu_choi)
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
                    getString(R.string.qua_xu)
                }
                "PHONE_CARD" -> {
                    getString(R.string.qua_the_cao)
                }
                "RECEIVE_STORE" -> {
                    getString(R.string.qua_nhan_tai_cua_hang)
                }
                "PRODUCT" -> {
                    getString(R.string.qua_hien_vat)
                }
                "VOUCHER" -> {
                    getString(R.string.voucher)
                }
                else -> getString(R.string.qua_tinh_than)
            }
            itemView.btnDoiQua.apply {
                if (obj.gift?.type == "VOUCHER") {
                    itemView.layoutCodeGift.setGone()
                    setVisible()
                    when {
                        obj.voucher?.can_use == true -> {
                            text = context.getString(R.string.dung_ngay)
                            setOnClickListener {
                                itemView.context.startActivity(Intent(itemView.context, VoucherLoyaltyActivity::class.java).apply {
                                    putExtra(ConstantsLoyalty.DATA_1, obj.voucher?.code)
                                    putExtra(ConstantsLoyalty.DATA_2, itemView.tvDateTime.text.toString().trim())
                                    putExtra(ConstantsLoyalty.DATA_3, obj.owner?.logo?.thumbnail)
                                })
                            }
                        }
                        obj.voucher?.can_mark_use == true -> {
                            text = context.getString(R.string.danh_dau_da_dung)

                            setOnClickListener {
                                showCustomErrorToast(itemView.context, context.getString(R.string.chua_co_su_kien))
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

            itemView.webViewUrl.settings.javaScriptEnabled = true
            itemView.webViewUrl.loadDataWithBaseURL(null, obj.gift?.description
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
                "${TextHelper.formatMoneyPhay(obj.loyalty_gift?.quantity_remain)} ${getString(R.string.qua)}"
            } else {
                "0 ${getString(R.string.qua)}"
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
                    getString(R.string.qua_xu)
                }
                "PHONE_CARD" -> {
                    setOnClickButton(obj)
                    getString(R.string.qua_the_cao)
                }
                "RECEIVE_STORE" -> {
                    itemView.btnDoiQua.setText(R.string.huong_dan_doi_qua)
                    itemView.btnDoiQua.setOnClickListener {
                        DialogHelperGame.dialogTutorialLoyalty(itemView.context, R.drawable.bg_gradient_button_blue)
                    }
                    getString(R.string.qua_nhan_tai_cua_hang)
                }
                "PRODUCT" -> {
                    itemView.layoutPhiVanChuyen.setVisible()
                    setOnClickButton(obj)
                    getString(R.string.qua_hien_vat)
                }
                "VOUCHER" -> {
                    setOnClickButton(obj)
                    getString(R.string.qua_voucher)
                }
                else -> {
                    itemView.btnDoiQua.setGone()
                    getString(R.string.qua_tinh_than)
                }
            }

            itemView.webViewUrl.settings.javaScriptEnabled = true
            itemView.webViewUrl.loadDataWithBaseURL(null, obj.loyalty_gift?.gift?.description
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
                        R.drawable.ic_error_scan_game,
                        getString(R.string.ban_khong_du_diem_doi_qua),
                        getString(R.string.tich_cuc_tham_gia_cac_chuong_tring_cua_nhan_hang_de_nhan_diem_thanh_vien_nhé),
                        null,
                        getString(R.string.tich_diem_ngay),
                        false,
                        R.drawable.bg_button_not_enough_point_blue_v2,
                        R.color.blueVip,
                        object : IClickButtonDialog<ICKNone> {
                            override fun onClickButtonData(data: ICKNone?) {
                                itemView.context.startActivity(
                                    Intent(
                                        itemView.context,
                                        CampaignOfBusinessActivity::class.java
                                    ).apply {
                                        putExtra(ConstantsLoyalty.DATA_1, obj.business?.id)
                                    })
                            }
                        },
                        object : IDismissDialog {
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
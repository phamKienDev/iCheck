package vn.icheck.android.loyalty.screen.gift_detail_voucher

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_gift_detail_voucher_staff.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.base.ICKViewType
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.setGone
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.loyalty.helper.TimeHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKScanVoucher

internal class GiftVoucherStaffAdapter : RecyclerViewCustomAdapter<ICKScanVoucher>() {

    var listenerClick: IClickListener? = null

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

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICKScanVoucher>(R.layout.item_gift_detail_voucher_staff, parent) {

        @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
        override fun bind(obj: ICKScanVoucher) {

            WidgetHelper.loadImageUrl(itemView.imgBanner, obj.campaign_image?.original)
            WidgetHelper.loadImageUrlRounded6(itemView.imgProduct, obj.image?.original)

            /**
             * @param status = false,
             * @param code = START_TIME_CAN_USE => voucher chưa đến hạn sử dụng
             * @param code = MAX_NUM_OF_USED_VOUCHER => voucher quá số lần sử dụng
             * @param code = END_TIME_CAN_USE => voucher quá hạn hạn sử dụng
             * @param code = EFFECTIVE_TIME => voucher quá hạn sử dụng
             * @param code = MAX_NUM_OF_USED_CUSTOMER => quá lượt sử dụng của mỗi khách hàng
             */

            if (obj.voucher?.checked_condition?.status == false) {
                itemView.tvTitleDate.setText(R.string.han_su_dung)

                if (obj.voucher.checked_condition?.code == "START_TIME_CAN_USE") {

                    itemView.tvTitleDate.setText(R.string.co_hieu_luc_tu)

                    itemView.tvDateTime.text = TimeHelper.convertDateTimeSvToDateVn(obj.voucher.start_at)

                    itemView.tvStatus.apply {
                        text = context.getString(R.string.chua_co_hieu_luc)
                        setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                        setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                    }

                } else if (obj.voucher.checked_condition?.code == "MAX_NUM_OF_USED_VOUCHER" || obj.voucher.checked_condition?.code == "MAX_NUM_OF_USED_CUSTOMER") {

                    itemView.layoutDate.setGone()

                    itemView.tvStatus.apply {
                        text = context.getString(R.string.het_luot_su_dung)
                        setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                        setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                    }

                } else {

                    itemView.tvDateTime.text = ""

                    itemView.tvStatus.apply {
                        text = context.getString(R.string.het_han_su_dung)
                        setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                        setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                    }
                }

            } else {
                itemView.tvTitleDate.setText(R.string.han_su_dung)

                itemView.tvDateTime.text = obj.voucher?.let { TimeHelper.timeGiftVoucher(it) }

                itemView.tvStatus.apply {

                    if (itemView.tvDateTime.text.toString() == itemView.context.getString(R.string.con_lai_)) {

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

            itemView.tvProduct.text = if (!obj.name.isNullOrEmpty()) {
                obj.name
            } else {
                default
            }

            itemView.tvVanChuyen.setText(R.string.voucher)

            itemView.btnDoiQua.apply {
                if (obj.rewardType == "VOUCHER") {
                    setVisible()
                    if (obj.voucher?.checked_condition?.status == true) {
                        when (obj.voucher.can_mark_use) {
                            true -> {
                                text = context.getString(R.string.danh_dau_da_dung)

                                setOnClickListener {
                                    listenerClick?.onClickUsedButton(obj)
                                }
                            }
                            else -> {
                                setGone()
                            }
                        }
                    } else {
                        text = context.getString(R.string.tiep_tuc_quet)

                        setOnClickListener {
                            listenerClick?.onClickScanButton(obj)
                        }
                    }
                } else {
                    setGone()
                }
            }

            itemView.webViewUrl.settings.javaScriptEnabled = true
            itemView.webViewUrl.loadDataWithBaseURL(null, obj.desc
                    ?: "", "text/html; charset=utf-8", "UTF-8", null)

            WidgetHelper.loadImageUrl(itemView.imgAvatar, obj.shop_image)

            itemView.tvNameShop.text = if (!obj.shop_name.isNullOrEmpty()) {
                obj.shop_name
            } else {
                default
            }
        }
    }

    fun setListener(listener: IClickListener) {
        listenerClick = listener
    }

    interface IClickListener {
        fun onClickUsedButton(obj: ICKScanVoucher)
        fun onClickScanButton(obj: ICKScanVoucher)
    }
}
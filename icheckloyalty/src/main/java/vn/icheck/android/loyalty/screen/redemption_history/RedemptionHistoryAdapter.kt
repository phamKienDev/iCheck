package vn.icheck.android.loyalty.screen.redemption_history

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_redemption_history.view.*
import vn.icheck.android.ichecklibs.util.RStringUtils.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.TimeHelper
import vn.icheck.android.loyalty.helper.TimeHelper.millisecondEffectiveTime
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKBoxGifts
import vn.icheck.android.loyalty.model.ICKRedemptionHistory
import vn.icheck.android.loyalty.model.ICKRewardGameLoyalty
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.detail_gift_point.DetailGiftLoyaltyActivity
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.home.HomeRedeemPointActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.giftdetail.GiftDetailActivity
import vn.icheck.android.loyalty.sdk.LoyaltySdk

internal class RedemptionHistoryAdapter(callback: IRecyclerViewCallback) : RecyclerViewCustomAdapter<Any>(callback) {

    fun setDataICKRedemptionHistory(obj: MutableList<ICKRedemptionHistory>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addDataICKRedemptionHistory(obj: MutableList<ICKRedemptionHistory>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun setDataICKRewardGameLoyalty(obj: MutableList<ICKRewardGameLoyalty>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addDataICKRewardGameLoyalty(obj: MutableList<ICKRewardGameLoyalty>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
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
            listData[position].apply {
                when (this) {
                    is ICKRedemptionHistory -> {
                        holder.bind(this)
                    }
                    is ICKRewardGameLoyalty -> {
                        holder.bind(this)
                    }
                }
            }
        } else {
            super.onBindViewHolder(holder, position)
        }
    }

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_redemption_history, parent, false)) {
        val default = itemView.context.getString(R.string.dang_cap_nhat)

        fun bind(obj: ICKRewardGameLoyalty) {
            itemView.tvTitle.text = if (obj.gift?.name.isNullOrEmpty()) {
                itemView.context.getString(R.string.dang_cap_nhat)
            } else {
                obj.gift?.name
            }

            itemView.tvDate.text = if (obj.created_at.isNullOrEmpty()) {
                itemView.context.getString(R.string.dang_cap_nhat)
            } else {
                rText(R.string.thoi_gian_doi_s, TimeHelper.convertDateTimeSvToTimeDateVn(obj.created_at))
            }

            WidgetHelper.loadImageUrl(itemView.imgGiftFromVendor, obj.gift?.image?.medium, R.drawable.emty_reward)

            val data = ICKBoxGifts()
            data.gift = obj.gift
            data.points = obj.points
            data.export_gift_from = obj.campaign?.export_gift_from
            data.export_gift_to = obj.campaign?.export_gift_to

            if (obj.gift?.type == "ICOIN") {
                itemView.tvState.setInvisible()
                itemView.btnManagerPoint.setVisible()

                itemView.btnManagerPoint.setOnClickListener {
                    LoyaltySdk.openActivity("point_transitions")
                }

                itemView.setOnClickListener {
                    LoyaltySdk.openActivity("point_transitions")
                }

            } else {
                itemView.btnManagerPoint.setInvisible()
                itemView.tvState.setVisible()

                if (obj.gift?.type == "VOUCHER") {
                    itemView.tvState.run {
                        if (obj.voucher != null) {

                            data.voucher = obj.voucher

                            data.titleDate = rText(R.string.han_su_dung)

                            if (obj.voucher?.checked_condition?.status == false) {
                                if (obj.voucher?.checked_condition?.code == "START_TIME_CAN_USE") {

                                    data.titleDate = rText(R.string.co_hieu_luc_tu)

                                    data.dateChange = TimeHelper.convertDateTimeSvToDateVn(obj.voucher?.start_at)

                                    data.statusChange = rText(R.string.chua_co_hieu_luc)

                                    data.colorText = ContextCompat.getColor(itemView.context, R.color.orange)

                                    data.colorBackground = R.drawable.bg_corner_30_orange_opacity_02
                                } else if (obj.voucher?.checked_condition?.code == "MAX_NUM_OF_USED_VOUCHER" || obj.voucher?.checked_condition?.code == "MAX_NUM_OF_USED_CUSTOMER") {

                                    data.statusChange = rText(R.string.het_luot_su_dung)

                                    data.colorText = ContextCompat.getColor(itemView.context, R.color.errorColor)

                                    data.colorBackground = R.drawable.bg_corner_30_red_opacity_02
                                } else {

                                    data.dateChange = ""

                                    data.statusChange = rText(R.string.het_han_su_dung)
                                    data.colorText = ContextCompat.getColor(itemView.context, R.color.errorColor)

                                    data.colorBackground = R.drawable.bg_corner_30_red_opacity_02
                                }
                            } else {

                                data.dateChange = TimeHelper.timeGiftVoucher(obj.voucher!!)

                                if (data.dateChange == rText(R.string.con_lai_)) {

                                    data.dateChange = ""

                                    data.statusChange = rText(R.string.het_han_su_dung)

                                    data.colorText = ContextCompat.getColor(itemView.context, R.color.errorColor)

                                    data.colorBackground = R.drawable.bg_corner_30_red_opacity_02
                                } else {
                                    data.statusChange = rText(R.string.co_the_su_dung)

                                    data.colorText = ContextCompat.getColor(itemView.context, R.color.green2)

                                    data.colorBackground = R.drawable.bg_corner_30_green_opacity_02
                                }
                            }
                        }

                        text = data.statusChange
                        setTextColor(data.colorText)
                        setBackgroundResource(data.colorBackground)
                    }
                } else {
                    itemView.tvState.run {
                        when (obj.winner?.status) {
                            "new" -> {
                                data.state = 1
                                text = rText(R.string.cho_xac_nhan)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                            }
                            "waiting_receive_gift" -> {
                                data.state = 2
                                visibility = View.VISIBLE
                                text = rText(R.string.cho_giao)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                            }
                            "received_gift" -> {
                                data.state = 3
                                visibility = View.VISIBLE
                                text = rText(R.string.da_nhan_qua)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                                setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
                            }
                            "refused_gift" -> {
                                data.state = 4
                                visibility = View.VISIBLE
                                text = rText(R.string.tu_choi)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                            }
                            else -> {
                                data.state = 5
                                visibility = View.GONE
                            }
                        }
                    }
                }

                itemView.setOnClickListener {
                    DetailGiftLoyaltyActivity.obj = data
                    itemView.context.startActivity(Intent(itemView.context, DetailGiftLoyaltyActivity::class.java).apply {
//                        putExtra(ConstantsLoyalty.DATA_1, data)
                        putExtra(ConstantsLoyalty.DATA_2, HomeRedeemPointActivity.banner)
                        putExtra(ConstantsLoyalty.DATA_3, HomeRedeemPointActivity.campaignID)
                        putExtra(ConstantsLoyalty.DATA_7, 2)
                    })
                }
            }
        }

        fun bind(obj: ICKRedemptionHistory) {
            WidgetHelper.loadImageUrlRounded6(itemView.imgGiftFromVendor, obj.gift?.image?.medium)

            itemView.tvTitle.text = if (!obj.gift?.name.isNullOrEmpty()) {
                obj.gift?.name
            } else {
                default
            }

            itemView.tvDate.text = if (!obj.win_at.isNullOrEmpty()) {
                rText(R.string.thoi_gian_doi_s, TimeHelper.convertDateTimeSvToTimeDateVn(obj.win_at))
            } else {
                default
            }

            if (obj.gift?.type != "ICOIN") {
                itemView.btnManagerPoint.setInvisible()

                itemView.tvState.setVisible()

                if (obj.gift?.type == "VOUCHER") {
                    if (obj.voucher != null) {
                        itemView.tvState.run {
                            if (obj.voucher?.checked_condition?.status == false) {

                                if (obj.voucher?.checked_condition?.code == "START_TIME_CAN_USE") {

                                    text = rText(R.string.chua_co_hieu_luc)
                                    setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                    setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                                } else if (obj.voucher?.checked_condition?.code == "MAX_NUM_OF_USED_VOUCHER" || obj.voucher?.checked_condition?.code == "MAX_NUM_OF_USED_CUSTOMER") {

                                    text = rText(R.string.het_luot_su_dung)
                                    setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                                    setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                                } else {

                                    text = rText(R.string.het_han_su_dung)
                                    setTextColor(ContextCompat.getColor(itemView.context, R.color.errorColor))
                                    setBackgroundResource(R.drawable.bg_corner_30_red_opacity_02)
                                }
                            } else {
                                text = rText(R.string.co_the_su_dung)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                                setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
                            }
                        }
                    } else {
                        itemView.tvState.setGone()
                    }
                } else {
                    itemView.tvState.run {
                        when (obj.status) {
                            "new" -> {
                                text = rText(R.string.cho_xac_nhan)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                            }
                            "waiting_receive_gift" -> {
                                text = rText(R.string.cho_giao)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                            }
                            "received_gift" -> {
                                text = rText(R.string.da_nhan_qua)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                                setBackgroundResource(R.drawable.bg_corner_30_green_opacity_02)
                            }
                            "refused_gift" -> {
                                text = rText(R.string.tu_choi)
                                setTextColor(ContextCompat.getColor(itemView.context, R.color.orange))
                                setBackgroundResource(R.drawable.bg_corner_30_orange_opacity_02)
                            }
                            else -> {
                                setGone()
                            }
                        }
                    }
                }

                itemView.setOnClickListener {
                    GiftDetailActivity.startActivityGiftDetail(itemView.context, obj.id ?: -1)
                }
            } else {
                itemView.tvState.setInvisible()
                itemView.btnManagerPoint.setVisible()

                itemView.btnManagerPoint.setOnClickListener {
                    LoyaltySdk.openActivity("point_transitions")
                }

                itemView.setOnClickListener {
                    LoyaltySdk.openActivity("point_transitions")
                }
            }
        }
    }
}
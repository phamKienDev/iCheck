package vn.icheck.android.loyalty.screen.loyalty_customers.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_header_earn_long_term_points.view.*
import kotlinx.android.synthetic.main.item_header_earn_long_term_points.view.tvPoint
import kotlinx.android.synthetic.main.item_transaction_history.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.commons.RecyclerViewCustomAdapter
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKError
import vn.icheck.android.loyalty.model.ICKLongTermProgram
import vn.icheck.android.loyalty.model.ICKTransactionHistory
import vn.icheck.android.loyalty.network.SessionManager
import vn.icheck.android.loyalty.screen.loyalty_customers.campaign_of_business.CampaignOfBusinessActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.gift_shop.GiftShopActivity
import vn.icheck.android.loyalty.screen.loyalty_customers.history.LoyaltyPointsHistoryActivity
import vn.icheck.android.loyalty.screen.redemption_history.RedemptionHistoryActivity

internal class HomePageEarnPointsAdapter(private val banner: String?, private val id: Long?) : RecyclerViewCustomAdapter<Any>() {

    fun addHeader(obj: ICKLongTermProgram) {
        listData.clear()
        if (listData.isNullOrEmpty()) {
            listData.add(obj)
        } else {
            listData.add(0, obj)
        }
        notifyDataSetChanged()
    }

    fun addItem(obj: MutableList<ICKTransactionHistory>) {
        for (i in listData.size - 1 downTo 0) {
            if (listData[i] is ICKTransactionHistory) {
                listData.removeAt(i)
            }
        }
        listData.addAll(obj)

        if (listData.size > 7) {
            listData.add(listData.size, 1)
        }
        notifyDataSetChanged()
    }

    fun setErrorNotEmpty(error: ICKError) {
        isLoadMore = false
        isLoading = false

        listData.add(listData.size, true)
        setMessage(error.icon, error.title, error.message, error.textButton, error.backgroundButton)
        notifyDataSetChanged()
    }

    override fun getItemType(position: Int): Int {
        return when (listData[position]) {
            is ICKLongTermProgram -> {
                ICKViewType.HEADER_TYPE
            }
            is Int -> {
                ICKViewType.BUTTON_TYPE
            }
            is Boolean -> {
                ICKViewType.SHORT_MESSAGE_TYPE
            }
            else -> {
                ICKViewType.ITEM_TYPE
            }
        }
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICKViewType.HEADER_TYPE -> HeaderHolder(parent)
            ICKViewType.BUTTON_TYPE -> ButtonHolder(parent)
            else -> TransactionHistoryHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is HeaderHolder -> {
                if (listData[position] is ICKLongTermProgram) {
                    holder.bind(listData[position] as ICKLongTermProgram)
                }
            }
            is TransactionHistoryHolder -> {
                if (listData[position] is ICKTransactionHistory) {
                    holder.bind(listData[position] as ICKTransactionHistory)
                }
            }
            is ButtonHolder -> {
                holder.itemView.setOnClickListener {
                    holder.itemView.context.startActivity(Intent(holder.itemView.context, LoyaltyPointsHistoryActivity::class.java).apply {
                        putExtra(ConstantsLoyalty.ID, id ?: -1)
                    })
                }
            }
            else -> {
                super.onBindViewHolder(holder, position)
            }
        }
    }

    inner class HeaderHolder(parent: ViewGroup) : BaseViewHolder<ICKLongTermProgram>(R.layout.item_header_earn_long_term_points, parent) {

        override fun bind(obj: ICKLongTermProgram) {
            WidgetHelper.loadImageUrl(itemView.imgBanner, banner)

            val name = if (obj.customer?.name.isNullOrEmpty()) {
                "Chào ${SessionManager.session.user?.name},"
            } else {
                "Chào ${obj.customer?.name},"
            }

            itemView.tvNameUser.text = if (name.contains("null")) {
                "Chào bạn,"
            } else {
                name
            }

            itemView.tvPoint.text = TextHelper.formatMoneyPhay(obj.point?.points
                    ?: obj.customer?.points)

            itemView.tvCategoryPoint.text = obj.point_name ?: obj.network?.point_name

            itemView.btnCampaign.setOnClickListener {
                itemView.context.startActivity(Intent(itemView.context, CampaignOfBusinessActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.DATA_1, id ?: obj.user_id)
                })
            }

            itemView.btnStore.setOnClickListener {
                itemView.context.startActivity(Intent(itemView.context, GiftShopActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.ID, id ?: obj.user_id)
                })
            }

            itemView.btnMyGift.setOnClickListener {
                itemView.context.startActivity(Intent(itemView.context, RedemptionHistoryActivity::class.java).apply {
                    putExtra(ConstantsLoyalty.DATA_1, id ?: obj.user_id)
                    putExtra(ConstantsLoyalty.DATA_2, 1)
                })
            }
        }
    }

    inner class ButtonHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false))

    inner class TransactionHistoryHolder(parent: ViewGroup) : BaseViewHolder<ICKTransactionHistory>(R.layout.item_transaction_history, parent) {

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICKTransactionHistory) {
            if (adapterPosition == 0) {
                itemView.title.setVisible()
            } else {
                itemView.title.setGone()
            }

            checkNullOrEmpty(itemView.tvName, obj.reason)

            checkNullOrEmptyConvertDateTimeSvToTimeDateVn(itemView.tvDate, obj.created_at)

            when (obj.type?.code) {
                "GRANT" -> {
                    itemView.imgCategory.setImageResource(R.drawable.ic_xu_add_20px)

                    itemView.tvPoint.apply {
                        text = "+" + TextHelper.formatMoney(obj.point)
                        setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                    }
                }
                "SPEND" -> {
                    itemView.imgCategory.setImageResource(R.drawable.ic_xu_contract_20px)

                    itemView.tvPoint.apply {
                        text = "-" + TextHelper.formatMoneyPhay(obj.point)
                        setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccentRed))
                    }
                }
                else -> {
                    itemView.imgCategory.setImageResource(R.drawable.ic_xu_add_20px)

                    itemView.tvPoint.apply {
                        text = "+" + TextHelper.formatMoney(obj.point)
                        setTextColor(ContextCompat.getColor(itemView.context, R.color.green2))
                    }
                }
            }
        }
    }
}
package vn.icheck.android.screen.user.coinhistory

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_header_coin_history.view.*
import kotlinx.android.synthetic.main.item_history_transaction_pv_combank.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICCoinHistory
import vn.icheck.android.screen.user.campaign.holder.base.LoadingHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder

class CoinHistoryAdapter(val callback: ICoinHistoryView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listData = mutableListOf<ICCoinHistory?>()

    private var errorIcon: Int? = null
    private var errorTitle: String? = null
    private var errorMessage: String? = null
    private var buttonID: Int? = null
    private var isLoading = false
    private var isLoadmore = false

    private val headerType = 1
    private val titleType = 2
    private val itemType = 3
    private val loadingType = 4
    private val messageType = 5

    val isNotEmpity: Boolean
        get() {
            return listData.size > 1
        }

    private fun checkLoadmore(listCount: Int) {
        isLoadmore = listCount >= APIConstants.LIMIT
        isLoading = false
        errorIcon = null
        errorTitle = null
        errorMessage = null
    }

    fun setData(list: MutableList<ICCoinHistory>, filter: Boolean) {
        checkLoadmore(list.size)

        listData.clear()

        listData.add(null)
        if (filter) {
            listData.add(ICCoinHistory(-1L, 0, "Bộ lọc đã chọn"))
        } else {
            listData.add(ICCoinHistory(-1L, 0, "Lịch sử giao dịch"))
        }

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: MutableList<ICCoinHistory>) {
        checkLoadmore(list.size)

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setError(icon: Int, title: String? = null, message: String? = null, buttonID: Int? = null) {
        isLoading = false
        isLoadmore = false

        errorIcon = icon
        errorTitle = title
        errorMessage = message
        this.buttonID = buttonID

        listData.clear()
        notifyDataSetChanged()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setEmpityData() {
        for (i in 0 until listData.size) {
            if (listData[i] == null && i != 0) {
                listData.removeAt(i)
                notifyItemRemoved(i)
                break
            }
        }
        setError(R.drawable.ic_group_120dp, "Chưa có lịch sử giao dịch", null, -1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            headerType -> HeaderCoinHolder(parent)
            itemType -> ItemCoinHolder(parent)
            titleType -> TitleHolder(parent)
            messageType -> MessageHolder(parent)
            else -> LoadingHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderCoinHolder -> {
                holder.bind(TextHelper.formatMoney(SettingManager.getUserCoin))
            }
            is ItemCoinHolder -> {
                holder.bind(listData[position]!!)
            }
            is TitleHolder -> {
                holder.bind(listData[position]!!)
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    callback.onLoadmore()
                }
            }
            is MessageHolder -> {
                holder.itemView.layoutParams = ViewHelper.createLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                    it.topMargin = SizeHelper.dpToPx(130)
                }
                holder.bind(errorIcon, errorTitle, errorMessage, buttonID)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (errorMessage != null || errorTitle != null) {
            2
        } else {
            if (isNotEmpity) {
                if (isLoadmore) {
                    listData.size + 1
                } else {
                    listData.size
                }
            } else if (listData.size == 1) {
                2
            } else {
                0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> headerType
            else -> {
                if (errorMessage != null || errorTitle != null) {
                    messageType
                } else {
                    if (isNotEmpity) {
                        if (position < listData.size) {
                            if (listData[position]?.id == -1L) {
                                titleType
                            } else {
                                itemType
                            }
                        } else {
                            loadingType
                        }
                    } else {
                        messageType
                    }
                }
            }
        }
    }

    inner class HeaderCoinHolder(parent: ViewGroup) : BaseViewHolder<String>(LayoutInflater.from(parent.context).inflate(R.layout.item_header_coin_history, parent, false)) {
        override fun bind(obj: String) {
            itemView.tvCoin.text = "${TextHelper.formatMoneyPhay(SessionManager.getCoin())} Xu"
        }
    }

    inner class TitleHolder(parent: ViewGroup) : BaseViewHolder<ICCoinHistory>(createTitle(parent.context)) {
        override fun bind(obj: ICCoinHistory) {
            (itemView as AppCompatTextView).run {
                text = obj.title
            }
        }
    }

    fun createTitle(context: Context): AppCompatTextView {
        return ViewHelper.createText(
                context,
                ViewHelper.createLayoutParams().also {
                    it.setMargins(SizeHelper.size12, SizeHelper.size20, 0, SizeHelper.size8)
                },
                null,
                Typeface.createFromAsset(context.assets, "font/barlow_semi_bold.ttf"),
                ContextCompat.getColor(context, R.color.blue),
                18f)
    }

    private inner class ItemCoinHolder(parent: ViewGroup) : BaseViewHolder<ICCoinHistory>(LayoutInflater.from(parent.context).inflate(R.layout.item_history_transaction_pv_combank, parent, false)) {
        override fun bind(obj: ICCoinHistory) {
            itemView.tvMoney.text = TextHelper.formatMoneyPhay(obj.amount)

            if (obj.type == 1) {
                itemView.imgType.setImageResource(R.drawable.ic_xu_add_20px)
                itemView.tvMoney.setTextColor(Color.parseColor("#85C440"))
            } else {
                itemView.imgType.setImageResource(R.drawable.ic_xu_contract_20px)
                itemView.tvMoney.setTextColor(Color.parseColor("#FF0000"))
            }

            itemView.tvMission.text = obj.title
            itemView.tvContent.text = obj.description

            itemView.tvTime.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(obj.createdAt)

        }
    }
}
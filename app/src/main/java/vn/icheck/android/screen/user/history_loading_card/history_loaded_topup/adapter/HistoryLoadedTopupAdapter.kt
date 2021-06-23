package vn.icheck.android.screen.user.history_loading_card.history_loaded_topup.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_history_loaded_topup.view.*
import kotlinx.android.synthetic.main.item_error_history_topup.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.screen.user.history_loading_card.history_loaded_topup.view.IHistoryLoadedTopupView
import vn.icheck.android.util.kotlin.WidgetUtils

class HistoryLoadedTopupAdapter constructor(val view: IHistoryLoadedTopupView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICRechargePhone>()

    private var errorCode = 0
    private var isLoadMore = true
    private var isLoading = true

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun setListData(list: MutableList<ICRechargePhone>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICRechargePhone>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun clearListData() {
        errorCode = 0
        isLoadMore = false
        isLoading = false
        listData.clear()
        notifyDataSetChanged()
    }

    fun showLoading() {
        listData.clear()
        errorCode = 0
        isLoadMore = true

        notifyDataSetChanged()
    }

    fun setErrorCode(error: Int) {
        listData.clear()
        errorCode = error
        isLoadMore = false

        notifyDataSetChanged()
    }

    fun removeDataWithoutUpdate() {
        listData.clear()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_history_loaded_topup, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_error_history_topup, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore) listData.size + 1
            else listData.size
        } else {
            if (errorCode == 0) 0
            else 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size) itemType
            else loadType
        } else {
            if (isLoadMore) loadType
            else showType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)
            }
            is LoadHolder -> {
                holder.bind()
                if (!isLoading) {
                    view.onLoadMore()
                    isLoading = true
                }
            }
            is MessageHolder -> {
                holder.bind(errorCode)

                holder.itemView.setOnClickListener {
                    view.onRefresh()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ICRechargePhone) {
            WidgetUtils.loadImageFitCenterUrl(itemView.imgTopup, item.avatar)
            itemView.layoutImg.background= ViewHelper.bgWhiteStrokeGrayD4Corners8(itemView.context)
            itemView.tvNameNetwork.text = "Nạp thẻ ${item.provider}"

            if (item.denomination is String) {
                if (!(item.denomination as String).isNullOrEmpty()) {
                    itemView.tvPrice.text = TextHelper.formatMoneyPhay((item.denomination as String).toLong()) + "đ"
                } else {
                    itemView.tvPrice.text = itemView.context.getString(R.string.dang_cap_nhat)
                }
            }

            if (!item.phone.isNullOrEmpty()) {
                val phone = getPhoneOnly(item.phone)
                itemView.tvPhoneNumber.text = Constant.formatPhone(phone)
            } else {
                itemView.tvPhoneNumber.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            itemView.tvDate.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(item.createdAt)
        }

        private fun getPhoneOnly(phone: String?): String {
            return if (phone != null) {
                if (phone.startsWith("84")) {
                    StringBuilder(phone).apply {
                        replace(0, 2, "0")
                    }.toString()
                } else if (phone.startsWith("+84")) {
                    StringBuilder(phone).apply {
                        replace(0, 3, "0")
                    }.toString()
                } else {
                    phone
                }
            } else {
                "Chưa cập nhật"
            }
        }
    }

    class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(view.context), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    class MessageHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: Int) {
            when (errorCode) {
                Constant.ERROR_EMPTY -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_emty_history_topup)
                    itemView.txtMessage.text = "Bạn chưa nạp thẻ nào!"
                }

                Constant.ERROR_SERVER -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.txtMessage.text = itemView.context.getString(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)
                }

                Constant.ERROR_INTERNET -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    itemView.txtMessage.text = itemView.context.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai)
                }
            }
        }
    }

}
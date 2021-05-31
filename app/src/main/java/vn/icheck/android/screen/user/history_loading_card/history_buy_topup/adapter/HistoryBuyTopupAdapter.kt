package vn.icheck.android.screen.user.history_loading_card.history_buy_topup.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_error_history_topup.view.*
import kotlinx.android.synthetic.main.item_history_buy_topup.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.recharge_phone.RechargePhoneInteractor
import vn.icheck.android.network.models.ICNone
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.screen.user.history_loading_card.history_buy_topup.view.IHistoryBuyTopupView
import vn.icheck.android.util.ick.showSimpleSuccessToast
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class HistoryBuyTopupAdapter constructor(val view: IHistoryBuyTopupView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICRechargePhone>()

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

    fun removeDataWithoutUpdate() {
        listData.clear()
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

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    val isListNotEmpty: Boolean
        get() {
            return listData.isNotEmpty()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            itemType -> ViewHolder(inflater.inflate(R.layout.item_history_buy_topup, parent, false))
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_error_history_topup, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore)
                listData.size + 1
            else
                listData.size
        } else {
            if (errorCode == 0)
                0
            else
                1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.size > 0) {
            if (position < listData.size)
                itemType
            else
                loadType
        } else {
            if (isLoadMore)
                loadType
            else
                showType
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.bind(item)

                holder.itemView.tvLoadNow.setOnClickListener {
                    view.onClickLoadNow(item)
                }
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
        private val interactor = RechargePhoneInteractor()

        fun bind(item: ICRechargePhone) {
            WidgetUtils.loadImageFitCenterUrl(itemView.imgTopup, item.avatar)
            itemView.layoutImg.background=ViewHelper.bgWhiteStrokeGrayRadius8(itemView.context)

            itemView.tvNameNetwork.text = "Thẻ ${item.provider}"

            if (item.denomination is String) {
                if (!(item.denomination as String).isNullOrEmpty()) {
                    itemView.tvPriceTopup.text = TextHelper.formatMoneyPhay((item.denomination as String).toLong()) + "đ"
                } else {
                    itemView.tvPriceTopup.text = itemView.context.getString(R.string.dang_cap_nhat)
                }
            }

            if (!item.createdAt.isNullOrEmpty()) {
                itemView.tvDate.text = TimeHelper.convertDateTimeSvToTimeDateVnPhay(item.createdAt)
            } else {
                itemView.tvDate.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!item.card?.pin.isNullOrEmpty()) {
                itemView.tvMaThe.text = item.card?.pin
            } else {
                itemView.tvMaThe.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (!item.card?.serial.isNullOrEmpty()) {
                itemView.tvSeri.text = item.card?.serial
            } else {
                itemView.tvSeri.text = itemView.context.getString(R.string.dang_cap_nhat)
            }

            if (item.isUsed == true) {
                itemView.tvSelectUsed.visibility = View.GONE
                itemView.view.visibility = View.GONE
                itemView.tvLoadNow.visibility = View.GONE
                itemView.tvUsed.visibility = View.VISIBLE
            } else {
                itemView.tvSelectUsed.visibility = View.VISIBLE
                itemView.view.visibility = View.VISIBLE
                itemView.tvLoadNow.visibility = View.VISIBLE
                itemView.tvUsed.visibility = View.GONE
            }

            itemView.tvSelectUsed.setOnClickListener {
                item.id?.let { it1 ->
                    onTickUseTopup(it1)
                }
            }
        }

        private fun onTickUseTopup(id: Long) {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            interactor.onTickUseTopup(id, object : ICNewApiListener<ICResponse<ICNone>> {
                override fun onSuccess(obj: ICResponse<ICNone>) {
                    itemView.context.showSimpleSuccessToast("Bạn đã đánh dấu đã nạp thẻ này!")
                    itemView.tvSelectUsed.visibility = View.GONE
                    itemView.view.visibility = View.GONE
                    itemView.tvLoadNow.visibility = View.GONE
                    itemView.tvUsed.visibility = View.VISIBLE
                }

                override fun onError(error: ICResponseCode?) {
                    itemView.context.showSimpleSuccessToast(R.drawable.ic_waring_white_40_px, "Bạn đã đánh dấu đã nạp thẻ này!")
                }
            })
        }
    }

    class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            view.progressBar.indeterminateDrawable.setColorFilter(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(view.context), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    class MessageHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: Int) {
            when (errorCode) {
                Constant.ERROR_EMPTY -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_emty_history_topup)
                    itemView.txtMessage.text = "Bạn chưa mua thẻ nào!"
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
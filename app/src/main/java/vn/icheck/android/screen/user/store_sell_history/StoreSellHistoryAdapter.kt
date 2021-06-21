package vn.icheck.android.screen.user.store_sell_history

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_error_history_topup.view.*
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_store_sell_history.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.history.ICStoreNear
import vn.icheck.android.util.kotlin.WidgetUtils

class StoreSellHistoryAdapter constructor(val view: StoreSellHistoryView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICStoreNear>()

    private var errorCode = 0
    private var isLoadMore = true
    private var isLoading = true

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun setListData(list: MutableList<ICStoreNear>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICStoreNear>) {
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
            itemType -> ViewHolder(inflater.inflate(R.layout.item_store_sell_history, parent, false))
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

                holder.itemView.setOnClickListener {
                    view.onClickItem(listData, listData[position].id)
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
        fun bind(obj: ICStoreNear) {
            if (!obj.avatar.isNullOrEmpty()) {
                WidgetUtils.loadImageUrl(itemView.avaShop, obj?.avatar, R.drawable.ic_error_load_shop_40_px, R.drawable.ic_error_load_shop_40_px)
            } else {
                itemView.avaShop.setImageResource(R.drawable.ic_error_load_shop_40_px)
            }

            itemView.tvNameShop.text = obj.name ?: itemView.context.getString(R.string.dang_cap_nhat)

            if (obj.distance != null) {
                TextHelper.convertMtoKmV2(obj.distance!!,itemView.tvDistance)
            } else {
                itemView.tvDistance.setText(R.string.dang_cap_nhat)
            }
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
                    itemView.txtMessage.setText(R.string.khong_co_cua_hang_nao)
                }

                Constant.ERROR_SERVER -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.txtMessage.setText(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)
                }

                Constant.ERROR_INTERNET -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    itemView.txtMessage.setText(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai)
                }
            }
        }
    }

}
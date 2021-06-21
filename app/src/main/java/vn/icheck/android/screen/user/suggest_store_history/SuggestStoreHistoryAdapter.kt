package vn.icheck.android.screen.user.suggest_store_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_history_suggestion_store.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.history.ICSuggestStoreHistory
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.base.holder.LongMessageHolder
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableEndText
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableStartText
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.WidgetUtils

class SuggestStoreHistoryAdapter constructor(val view: SuggestStoreHistoryView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICSuggestStoreHistory>()

    private var errorCode = 0
    private var isLoadMore = true
    private var isLoading = true

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun setListData(list: MutableList<ICSuggestStoreHistory>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICSuggestStoreHistory>) {
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
            itemType -> ViewHolder(inflater.inflate(R.layout.item_history_suggestion_store, parent, false))
            loadType -> LoadingHolder(parent)
            else -> LongMessageHolder(parent)
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

                holder.itemView.tvCountProductOfShop.setOnClickListener {
                    view.onClickShowProduct(item)
                }

                holder.itemView.tvGoMap.setOnClickListener {
                    view.onClickGotoMap(item)
                }
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    view.onLoadMore()
                    isLoading = true
                }
            }
            is LongMessageHolder -> {
                when (errorCode) {
                    Constant.ERROR_EMPTY -> {
                        holder.bind(R.drawable.ic_error_emty_history_topup, holder.itemView.context.getString(R.string.khong_co_cua_hang_nao_gan_ban), 0)
                    }
                    Constant.ERROR_SERVER -> {
                        holder.bind(R.drawable.ic_error_request, holder.itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai), 0)
                    }
                    Constant.ERROR_INTERNET -> {
                        holder.bind(R.drawable.ic_error_network, holder.itemView.context.getString(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai), 0)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ICSuggestStoreHistory) {
            WidgetUtils.loadImageUrl(itemView.imgAvatar, item.avatar, R.drawable.ic_error_load_shop_40_px, R.drawable.ic_error_load_shop_40_px)
            itemView.tvNameShop.text = item.name ?: itemView.context.getString(R.string.dang_cap_nhat)
            itemView.tvCountRating.text = String.format("%.1f", item.rating)

            itemView.tvGoMap.fillDrawableStartText(R.drawable.ic_alternate_16_px,vn.icheck.android.ichecklibs.Constant.secondaryColor)

            itemView.tvCountProductOfShop.fillDrawableEndText(R.drawable.ic_down_light_blue_18_px)
            itemView.tvCountProductOfShop.setText(R.string.co_d_san_pham_co_san, item.numProductSell)
        }
    }
}
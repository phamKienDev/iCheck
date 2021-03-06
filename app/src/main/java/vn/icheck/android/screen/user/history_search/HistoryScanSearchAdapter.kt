package vn.icheck.android.screen.user.history_search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_load_more.view.*
import kotlinx.android.synthetic.main.item_error_history_topup.view.*
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.history.ICItemHistory
import vn.icheck.android.screen.user.scan_history.holder.ProductHistoryHolder

class HistoryScanSearchAdapter(val view: HistoryScanSearchView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICItemHistory>()

    private var errorCode = 0
    private var isLoadMore = false
    private var isLoading = false

    private val itemType = 1
    private val showType = 2
    private val loadType = 3

    fun setListData(list: MutableList<ICItemHistory>) {
        isLoadMore = list.size >= Constant.DEFAULT_ITEM_COUNT
        isLoading = false
        errorCode = 0
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICItemHistory>) {
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

    fun removeDataWithoutUpdate() {
        listData.clear()
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
            itemType -> ProductHistoryHolder(parent)
            loadType -> LoadHolder(inflater.inflate(R.layout.item_load_more, parent, false))
            else -> MessageHolder(inflater.inflate(R.layout.item_error_history_topup, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size > 0) {
            if (isLoadMore) listData.size + 1
            else listData.size
        } else {
            1
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
            is ProductHistoryHolder -> {
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
            }
        }
    }

    private class LoadHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.progressBar.indeterminateDrawable.setColorFilter(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(view.context), android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    private class MessageHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(errorCode: Int) {
            when (errorCode) {
                Constant.ERROR_EMPTY_SEARCH -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_group_120dp)
                    itemView.txtMessage.visibility = View.GONE

                    val layoutParams = ViewHelper.createLayoutParams()
                    layoutParams.setMargins(SizeHelper.size50, SizeHelper.size12, SizeHelper.size50, 0)
                    itemView.txtMessage2.visibility = View.VISIBLE
                    itemView.txtMessage2.layoutParams = layoutParams
                    itemView.txtMessage2.setText(R.string.khong_tim_thay_lich_su_quet_phu_hop_vui_long_thu_lai_voi_tu_khoa_khac)
                }

                Constant.ERROR_EMPTY -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_group_120dp)
                    itemView.txtMessage.visibility = View.GONE

                    val layoutParams = ViewHelper.createLayoutParams()
                    layoutParams.setMargins(SizeHelper.size50, SizeHelper.size12, SizeHelper.size50, 0)
                    itemView.txtMessage2.visibility = View.VISIBLE
                    itemView.txtMessage2.setText(R.string.khong_tim_thay_lich_su_quet_phu_hop_vui_long_thu_lai_voi_tu_khoa_khac)
                }

                Constant.ERROR_SERVER -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                    itemView.txtMessage.setText(R.string.co_loi_xay_ra_vui_long_thu_lai)
                    itemView.txtMessage2.visibility = View.INVISIBLE
                }

                Constant.ERROR_INTERNET -> {
                    itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                    itemView.txtMessage.setText(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
                    itemView.txtMessage2.visibility = View.INVISIBLE
                }
            }
        }
    }
}
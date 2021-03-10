package vn.icheck.android.screen.user.list_product_history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.screen.user.campaign.holder.base.LoadingHolder
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder

class ListProductHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listData = mutableListOf<ICProductTrend>()

    private var isLoading = false
    private var isLoadmore = false
    private var icError: ICError? = null

    private var isFirst = false
    private val PRODUCT_TYPE = 10

    fun checkLoadmore(list: MutableList<ICProductTrend>) {
        isLoadmore = list.size >= APIConstants.LIMIT
        isLoading = true
        isFirst = true
    }

    fun setData(list: MutableList<ICProductTrend>) {
        checkLoadmore(list)

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: MutableList<ICProductTrend>) {
        checkLoadmore(list)

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setError(error: ICError) {
        icError = error
        isFirst = true
        listData.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                PRODUCT_TYPE
            } else {
                Constant.TYPE_LOAD_MORE
            }
        } else {
            Constant.ERROR_SERVER
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            PRODUCT_TYPE -> ItemProductHolder(layoutInflater.inflate(R.layout.layout_list_product_history_holder, parent, false))
            Constant.ERROR_SERVER -> LongMessageHolder(parent)
            else -> LoadingHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (isFirst) {
            if (icError != null) {
                1
            } else {
                if (isLoadmore) {
                    listData.size + 1
                } else {
                    listData.size
                }
            }
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemProductHolder -> {
                holder.bind()
            }
            is LoadingHolder -> {
                if (isLoading) {
                    isLoading = false
                    ListProductHistoryActivity.INSTANCE?.loadMore()
                }
            }
            is LongMessageHolder -> {
                holder.bind(icError!!.icon, icError!!.message, R.string.thu_lai)
                holder.setListener(View.OnClickListener {
                    ListProductHistoryActivity.INSTANCE?.getData()
                })
            }
        }
    }

    class ItemProductHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {

        }
    }

}
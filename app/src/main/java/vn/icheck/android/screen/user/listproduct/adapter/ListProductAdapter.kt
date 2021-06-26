package vn.icheck.android.screen.user.listproduct.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.collection.vertical.AdsProductVerticalHolderV2
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.screen.user.listproduct.view.IListProductView

class ListProductAdapter(val listener: IListProductView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICProductTrend>()

    private var errorMessage: String? = null

    private var isLoading = false
    private var isLoadMore = false

    private val itemType = 1
    private val loadMoreType = 2
    private val messageType = 3

    private fun checkLoadMore(listCount: Int) {
        errorMessage = ""
        isLoadMore = listCount >= APIConstants.LIMIT
        isLoading = false
    }

    fun setData(list: MutableList<ICProductTrend>) {
        checkLoadMore(list.size)

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: MutableList<ICProductTrend>) {
        checkLoadMore(list.size)

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setError(error: String) {
        listData.clear()

        isLoadMore = false
        isLoading = false

        errorMessage = error
        notifyDataSetChanged()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    val totalItem: Int
        get() {
            return listData.size
        }

    val isEmpty: Boolean
        get() {
            return listData.isEmpty()
        }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        } else {
            if (errorMessage != null) {
                1
            } else {
                0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                loadMoreType
            }
        } else {
            if (errorMessage != null) {
                messageType
            } else {
                super.getItemViewType(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            itemType -> AdsProductVerticalHolderV2(parent)
            loadMoreType -> LoadingHolder(parent)
            else -> MessageHolder(layoutInflater.inflate(R.layout.item_message, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdsProductVerticalHolderV2 -> {
                holder.bind(listData[position])
            }
            is MessageHolder -> {
                errorMessage?.let {
                    holder.bind(it)
                }

                holder.itemView.setOnClickListener {
                    listener.onLayoutMessageClicked()
                }
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true

                    listener.onLoadMoreProduct()
                }
            }
        }
    }

    private class MessageHolder(view: View) : BaseViewHolder<String>(view) {

        override fun bind(obj: String) {
            itemView.txtMessage.text = if (obj.isEmpty()) {
                itemView.context.getString(R.string.khong_co_san_pham_nao)
            } else {
                obj
            }
        }
    }
}
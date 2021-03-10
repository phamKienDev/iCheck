package vn.icheck.android.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.callback.IRecyclerViewSearchCallback
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.screen.user.campaign.holder.base.LoadingHolder
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder
import vn.icheck.android.screen.user.search_home.result.holder.NotResultHolder

abstract class RecyclerViewSearchAdapter<T>(open val listener: IRecyclerViewSearchCallback? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected val listData = mutableListOf<T>()

    protected val itemType = 1
    protected val loadingType = 2
    protected val messageType = 3
    protected val empityType = 4

    private var isFirst = false

    private var isLoadMoreEnable = true
    private var isLoading = false
    private var isLoadMore = false

    protected var icon: Int = 0
    protected var message: String = ""
    protected var button: Int? = null


    protected abstract fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun setListData(list: MutableList<T>) {
        if (isLoadMoreEnable) {
            isLoadMore = list.size == APIConstants.LIMIT
            isLoading = false
        }
        isFirst = true

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<T>) {
        if (isLoadMoreEnable) {
            isLoadMore = list.size == APIConstants.LIMIT
            isLoading = false
        }
        isFirst = true

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setMessage(icon: Int, message: String, button: Int?) {
        this.icon = icon
        this.message = message
        this.button = button
        isFirst = true
    }

    fun setError(icon: Int, message: String, button: Int?) {
        listData.clear()
        isLoadMore = false
        isLoading = false
        isFirst = true
        setMessage(icon, message, button)
        notifyDataSetChanged()
    }

    fun disableLoading() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun disableLoadMore() {
        isLoadMoreEnable = false
        isLoading = false
        isLoadMore = false
    }

    val isEmpty: Boolean
        get() {
            return listData.isEmpty()
        }

    val isNotEmpty: Boolean
        get() {
            return listData.isNotEmpty()
        }

    val getListData: MutableList<T>
        get() {
            return listData
        }

    override fun getItemCount(): Int {
        return if (isFirst) {
            if (listData.isNotEmpty()) {
                if (isLoadMore) {
                    listData.size + 1
                } else {
                    listData.size
                }
            } else {
                1
            }
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                loadingType
            }
        } else {
            if (message.isNotEmpty()) {
                messageType
            } else {
                empityType
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> viewHolder(parent)
            loadingType -> LoadingHolder(parent)
            empityType -> NotResultHolder(parent)
            else -> LongMessageHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingHolder -> {
                if (isLoadMore) {
                    if (!isLoading) {
                        isLoading = true
                        listener?.onLoadMore()
                    }
                }
            }
            is LongMessageHolder -> {
                holder.bind(icon, message, button)

                holder.setListener(View.OnClickListener {
                    listener?.onMessageClicked()
                })
            }
            is NotResultHolder -> {
                holder.setListener(View.OnClickListener {
                    listener?.onNotResultClicked()
                })
            }
        }
    }
}
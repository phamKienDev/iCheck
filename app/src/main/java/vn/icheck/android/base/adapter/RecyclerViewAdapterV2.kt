package vn.icheck.android.base.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.base.model.ICError
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.base.holder.LongMessageHolder

abstract class RecyclerViewAdapterV2<T>(open val listener: IRecyclerViewCallback? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected val listData = mutableListOf<T>()

    protected val itemType = 1
    protected val loadingType = 2
    protected val messageType = 3

    private var isLoadMoreEnable = true
    private var isLoading = false
    private var isLoadMore = false

    protected var icon: Int = 0
    protected var message: String = ""
    protected var button: Int? = null

    protected abstract fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun setListData(list: MutableList<T>) {
        if (isLoadMoreEnable) {
            isLoadMore = list.size >= APIConstants.LIMIT
            isLoading = false
        }

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<T>) {
        if (isLoadMoreEnable) {
            isLoadMore = list.size >= APIConstants.LIMIT
            isLoading = false
        }

        listData.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Set nội dung cho ViewHolder hiện message
     *
     * @param icon: 0 là ẩn
     * @param message: Nội dung hiển thị
     * @param button: Null là sử dụng text mặc định (Thử lại), -1 là ẩn
     */
    fun setMessage(icon: Int, message: String, button: Int?) {
        this.icon = icon
        this.message = message
        this.button = button
    }

    fun setError(icon: Int, message: String, button: Int?) {
        listData.clear()
        isLoadMore = false
        isLoading = false

        setMessage(icon, message, button)
        notifyDataSetChanged()
    }

    fun setError(error: ICError) {
        listData.clear()
        isLoadMore = false
        isLoading = false

        error.message?.let { setMessage(error.icon, it, error.button) }
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
        return if (listData.isNotEmpty()) {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        } else {
            if (message.isNotEmpty()) {
                1
            } else {
                0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listData.size) {
            itemType
        } else {
            if (isLoadMore) {
                loadingType
            } else if (message.isNotEmpty()) {
                messageType
            } else {
                super.getItemViewType(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> viewHolder(parent)
            loadingType -> LoadingHolder(parent)
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
        }
    }
}
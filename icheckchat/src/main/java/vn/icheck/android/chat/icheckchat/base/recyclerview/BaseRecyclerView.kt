package vn.icheck.android.chat.icheckchat.base.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.LoadingHolder
import vn.icheck.android.chat.icheckchat.base.recyclerview.holder.LongMessageHolder
import vn.icheck.android.chat.icheckchat.base.view.MCViewType
import vn.icheck.android.chat.icheckchat.helper.NetworkHelper
import vn.icheck.android.chat.icheckchat.model.MCError

abstract class BaseRecyclerView<T>(open val listener: IRecyclerViewCallback? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected val listData = mutableListOf<T>()

    var isLoadMoreEnable = true
    var isLoading = false
    var isLoadMore = false

    protected var icon: Int = 0
    protected var message: String = ""
    protected var button: String? = null

    protected abstract fun getItemType(position: Int): Int
    protected abstract fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun setListData(list: MutableList<T>) {
        checkLoadmore(list)

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<T>) {
        checkLoadmore(list)

        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun checkLoadmore(list: MutableList<T>) {
        if (isLoadMoreEnable) {
            isLoadMore = list.size >= NetworkHelper.LIMIT
            isLoading = false
        }
    }

    fun checkLoadMore(listSize: Int) {
        isLoadMore = listSize >= NetworkHelper.LIMIT
        isLoading = false
    }

    /**
     * Set nội dung cho ViewHolder hiện message
     *
     * @param icon: 0 là ẩn
     * @param message: Nội dung hiển thị
     * @param button: Null là sử dụng text mặc định (Thử lại), -1 là ẩn
     */
    fun setMessage(icon: Int, message: String, button: String?) {
        this.icon = icon
        this.message = message
        this.button = button
    }

    fun setError(icon: Int, message: String, button: String?) {
        listData.clear()
        isLoadMore = false
        isLoading = false

        setMessage(icon, message, button)
        notifyDataSetChanged()
    }

    fun setError(error: MCError) {
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

    fun enableLoadMore(isEnable: Boolean) {
        isLoadMoreEnable = isEnable
        isLoadMore = isEnable
        isLoading = false
    }


    fun resetData(isRefresh: Boolean = false) {
        listData.clear()
        icon = 0
        message = ""
        button = null

        isLoadMoreEnable = false
        isLoading = false
        isLoadMore = false

        if (isRefresh) {
            notifyDataSetChanged()
        }
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
            getItemType(position)
        } else {
            when {
                isLoadMore -> {
                    MCViewType.TYPE_LOAD_MORE
                }
                message.isNotEmpty() -> {
                    MCViewType.TYPE_MESSAGE
                }
                else -> {
                    super.getItemViewType(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MCViewType.TYPE_LOAD_MORE -> LoadingHolder(parent)
            MCViewType.TYPE_MESSAGE -> LongMessageHolder(parent)
            else -> getViewHolder(parent, viewType)
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
                holder.bind(
                        icon,
                        "",
                        message,
                        button,
                        0,
                        0,
                        View.OnClickListener { listener?.onMessageClicked() })
            }
        }
    }
}
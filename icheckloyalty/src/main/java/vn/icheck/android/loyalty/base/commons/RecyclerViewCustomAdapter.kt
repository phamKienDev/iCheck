package vn.icheck.android.loyalty.base.commons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.network.APIConstants
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.model.ICKError

internal abstract class RecyclerViewCustomAdapter<T>(open val listener: IRecyclerViewCallback? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<T>()

    var isLoading = false
    var isLoadMore = false

    private var titleMsg = ""
    private var contentMsg: String? = null
    private var textButton: String? = null
    private var backgroundButton: Int? = null
    private var icon = 0
    private var colorButton = R.color.white
    private var colorMessage = R.color.colorSecondText

    protected abstract fun getItemType(position: Int): Int
    protected abstract fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun checkLoadMore(count: Int) {
        isLoadMore = count >= APIConstants.LIMIT
        isLoading = false
    }

    fun setListData(list: MutableList<T>) {
        checkLoadMore(list.size)

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<T>) {
        checkLoadMore(list.size)

        listData.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Set nội dung cho ViewHolder hiện message
     *
     * @param icon: 0 là ẩn
     * @param titleMsg: Title của message
     * @param contentMsg: Nội dung hiển thị
     * @param textButton: Message cửa button nếu muốn ẩn button thì truyền null hoặc rỗng
     * @param backgroundButton: background của button
     */
    fun setMessage(icon: Int, titleMsg: String?, contentMsg: String?, textButton: String?, backgroundButton: Int?, colorButton: Int = R.color.white, colorMessage: Int) {
        this.icon = icon
        this.titleMsg = titleMsg ?: ""
        this.contentMsg = contentMsg
        this.textButton = textButton
        this.backgroundButton = backgroundButton
        this.colorButton = colorButton
        this.colorMessage = colorMessage
    }

    fun setError(icon: Int, titleMsg: String?, contentMsg: String?, textButton: String?, backgroundButton: Int?, colorButton: Int) {
        listData.clear()
        isLoadMore = false
        isLoading = false

        setMessage(icon, titleMsg, contentMsg, textButton, backgroundButton, colorButton, R.color.colorSecondText)
        notifyDataSetChanged()
    }

    fun setError(error: ICKError) {
        listData.clear()
        isLoadMore = false
        isLoading = false

        setMessage(error.icon, error.title, error.message, error.textButton, error.backgroundButton, error.colorButton, error.colorMessage ?: R.color.colorSecondText)
        notifyDataSetChanged()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun resetData() {
        listData.clear()
        icon = 0
        titleMsg = ""
        contentMsg = null
        textButton = null
        backgroundButton = null
        colorButton = R.color.white

        isLoading = false
        isLoadMore = false
        notifyDataSetChanged()
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

    fun setLoadMore(position: Int): Boolean {
        return getItemViewType(position) == ICKViewType.LOADING_TYPE
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        } else {
            if (titleMsg.isNotEmpty()) {
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
                    ICKViewType.LOADING_TYPE
                }
                titleMsg.isNotEmpty() -> {
                    ICKViewType.MESSAGE_TYPE
                }
                else -> {
                    super.getItemViewType(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ICKViewType.LOADING_TYPE -> LoadingHolder(parent)
            ICKViewType.MESSAGE_TYPE -> MessageHolder(parent)
            ICKViewType.SHORT_MESSAGE_TYPE -> ShortMessageHolder(parent)
            else -> getViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ICKViewType.LOADING_TYPE -> {
                if (isLoadMore) {
                    if (!isLoading) {
                        isLoading = true
                        listener?.onLoadMore()
                    }
                }
            }
            ICKViewType.MESSAGE_TYPE -> {
                (holder as MessageHolder).bind(
                        icon,
                        titleMsg,
                        contentMsg,
                        textButton,
                        backgroundButton ?: 0,
                        colorButton,
                        { listener?.onMessageClicked() }, colorMessage)
            }
            ICKViewType.SHORT_MESSAGE_TYPE -> {
                (holder as ShortMessageHolder).bind(
                        icon,
                        titleMsg,
                        contentMsg,
                        textButton,
                        backgroundButton ?: 0,
                        colorButton
                ) { listener?.onMessageClicked() }
            }
        }
    }

    inner class LoadingHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_load_more, parent, false))

    class MessageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_loyalty_point, parent, false)) {

        fun bind(imgError: Int, msgError: String, msgErrorBottom: String?, msgButton: String?, bgButton: Int, colorButton: Int, callback: View.OnClickListener? = null, colorMessage: Int? = null) {
            itemView.findViewById<AppCompatImageView>(R.id.imgDefault).setImageResource(imgError)

            itemView.findViewById<AppCompatTextView>(R.id.tvMessageTop).apply {
                text = if (msgError.isEmpty()) {
                    ""
                } else {
                    msgError
                }
                setTextColor(ContextCompat.getColor(itemView.context, colorMessage
                        ?: R.color.colorSecondText))
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvMessageBottom).apply {
                if (msgErrorBottom.isNullOrEmpty()) {
                    setGone()
                } else {
                    setVisible()
                    text = msgErrorBottom
                }
                setTextColor(ContextCompat.getColor(itemView.context, colorMessage
                        ?: R.color.colorSecondText))
            }

            itemView.findViewById<AppCompatTextView>(R.id.btnDefault).apply {
                setBackgroundResource(bgButton)

                if (msgButton.isNullOrEmpty()) {
                    setInvisible()
                } else {
                    setVisible()
                    text = msgButton
                    setTextColor(ContextCompat.getColor(itemView.context, colorButton))
                }
                setOnClickListener(callback)
            }
        }
    }

    class ShortMessageHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_short_message_loyalty_point, parent, false)) {

        fun bind(imgError: Int, msgError: String, msgErrorBottom: String?, msgButton: String?, bgButton: Int, colorButton: Int, callback: View.OnClickListener? = null) {
            itemView.findViewById<AppCompatImageView>(R.id.imgDefault).setImageResource(imgError)

            itemView.findViewById<AppCompatTextView>(R.id.tvMessageTop).apply {
                text = if (msgError.isEmpty()) {
                    ""
                } else {
                    msgError
                }
            }

            itemView.findViewById<AppCompatTextView>(R.id.tvMessageBottom).apply {
                if (msgErrorBottom.isNullOrEmpty()) {
                    setGone()
                } else {
                    setVisible()
                    text = msgErrorBottom
                }
            }

            itemView.findViewById<AppCompatTextView>(R.id.btnDefault).apply {
                setBackgroundResource(bgButton)

                if (msgButton.isNullOrEmpty()) {
                    setInvisible()
                } else {
                    setVisible()
                    text = msgButton
                    setTextColor(ContextCompat.getColor(itemView.context, colorButton))
                }
                setOnClickListener(callback)
            }
        }
    }
}
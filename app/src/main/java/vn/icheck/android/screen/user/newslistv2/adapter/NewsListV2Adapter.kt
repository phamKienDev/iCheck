package vn.icheck.android.screen.user.newslistv2.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.news.NewsListV2Holder
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICNews
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder

class NewsListV2Adapter(val listener: IRecyclerViewCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICNews>()
    private val itemType = 1
    private val itemLoadMore = 2
    private val itemMessage = 3

    private var mMessageError: String? = null
    private var iconMessage = R.drawable.ic_default_loyalty
    private var isLoading = false
    private var isLoadMore = false

    private fun checkLoadMore(listCount: Int) {
        isLoadMore = listCount >= APIConstants.LIMIT
        isLoading = false
        mMessageError = ""
    }

    fun setError(error: String, icon: Int) {
        listData.clear()

        isLoadMore = false
        isLoading = false
        mMessageError = error
        iconMessage = icon
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    fun disableLoadMore(){
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setData(obj: List<ICNews>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: List<ICNews>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> NewsListV2Holder(parent)
            itemLoadMore -> LoadingHolder(parent)
            else -> MessageHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isEmpty()) {
            if (mMessageError != null) {
                1
            } else {
                0
            }
        } else {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                itemLoadMore
            }
        } else {
            if (mMessageError != null) {
                itemMessage
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsListV2Holder -> {
                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    listener.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(iconMessage, message = getString(R.string.chua_co_tin_tuc_nao), buttonID = -1)
                } else {
                    holder.bind(iconMessage, mMessageError!!)

                    holder.listener(View.OnClickListener {
                        listener.onMessageClicked()
                    })
                }
            }
        }
    }
}
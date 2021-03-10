package vn.icheck.android.screen.user.bookmark

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICProductTrend
import vn.icheck.android.screen.user.campaign.holder.base.LoadingHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder
import vn.icheck.android.screen.user.scan_history.holder.ProductHistoryHolder
import vn.icheck.android.screen.user.scan_history.view.IScanHistoryView

class BookMarkV2Adapter(val listener: IScanHistoryView, val view: IRecyclerViewCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICProductTrend>()

    private val itemType = 1
    private val itemLoadMore = 2

    private var mMessageError: String? = null
    private var iconMessage = R.drawable.ic_error_request
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
        iconMessage = icon
        mMessageError = error
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setData(obj: List<ICProductTrend>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: List<ICProductTrend>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ProductHistoryHolder(parent)
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
                ICViewTypes.MESSAGE_TYPE
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductHistoryHolder -> {
//                holder.bind(listData[position],null)
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    view.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(iconMessage, "Không có sản phẩm yêu thích nào!")
                } else {
                    holder.bind(iconMessage, mMessageError!!)
                }

                holder.listener(View.OnClickListener {
                    view.onMessageClicked()
                })
            }
        }
    }

}
package vn.icheck.android.screen.user.listdistributor

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.component.product.npp.adapter.DistributorAdapter.ViewHolder
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder

class ListDistributorAdapter(val listener: IRecyclerViewCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = mutableListOf<ICPage>()

    private val typeItem = 0
    private val typeLoadMore = 1

    var msgError: String? = null
    var iconError = R.drawable.ic_error_request
    var isLoadMore = false
    var isLoading = false

    fun checkLoadMore(countList: Int) {
        isLoadMore = countList >= APIConstants.LIMIT
        isLoading = false
        msgError = ""
    }

    fun setError(msg: String, icon: Int) {
        isLoadMore = false
        isLoading = false

        msgError = msg
        iconError = icon
        notifyDataSetChanged()
    }

    fun setData(obj: MutableList<ICPage>) {
        checkLoadMore(obj.size)

        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: MutableList<ICPage>) {
        checkLoadMore(obj.size)

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            typeItem -> ViewHolder(parent)
            typeLoadMore -> LoadingHolder(parent)
            else -> MessageHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isNullOrEmpty()) {
            if (msgError != null) {
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
                typeItem
            } else {
                typeLoadMore
            }
        } else {
            if (msgError != null) {
                ICViewTypes.MESSAGE_TYPE
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading){
                    isLoading = true
                    listener.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (msgError.isNullOrEmpty()){
                    holder.bind(iconError, "")
                }else{
                    holder.bind(iconError, msgError!!)
                }

                holder.listener(View.OnClickListener {
                    listener.onMessageClicked()
                })
            }
        }
    }
}
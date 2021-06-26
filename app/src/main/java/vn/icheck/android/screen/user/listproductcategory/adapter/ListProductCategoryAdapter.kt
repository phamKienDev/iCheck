package vn.icheck.android.screen.user.listproductcategory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.collection.vertical.AdsProductVerticalHolder
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.models.ICProduct
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.screen.user.listproductcategory.view.IListProductCategoryView

class ListProductCategoryAdapter(private val listener: IListProductCategoryView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listData = mutableListOf<ICProduct>()

    var isLoading = false
    var isLoadMore = false
    var messageError: String? = null

    private val itemType = 1
    private val itemLoadMore = 2
    private val itemMessage = 3

    private fun checkLoadMore(listCount: Int) {
        isLoading = false
        messageError = ""
        isLoadMore = listCount >= APIConstants.LIMIT
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setData(obj: MutableList<ICProduct>) {
        checkLoadMore(obj.size)

        listData.clear()

        for (i in obj.size - 1 downTo 0) {
            if (obj[i].thumbnails?.medium.isNullOrEmpty()) {
                obj.removeAt(i)
            }
        }

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: MutableList<ICProduct>) {
        checkLoadMore(obj.size)

        for (i in obj.size - 1 downTo 0) {
            if (obj[i].thumbnails?.medium.isNullOrEmpty()) {
                obj.removeAt(i)
            }
        }

        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun setError(error: String) {
        isLoading = false
        isLoadMore = false
        messageError = error
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    fun setLoadMore(position: Int): Boolean {
        return getItemViewType(position) == itemLoadMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            itemType -> AdsProductVerticalHolder(parent)
            itemLoadMore -> LoadingHolder(parent)
            else -> MessageHolder(layoutInflater.inflate(R.layout.item_message, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        } else {
            if (messageError != null) {
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
                itemLoadMore
            }
        } else {
            if (messageError != null) {
                itemMessage
            } else {
                super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AdsProductVerticalHolder -> {
                if (position >= listData.size - 20){
                    if (isLoadMore && !isLoading) {
                        isLoading = true
                        listener.onLoadMore()
                    }
                }

                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    listener.onLoadMore()
                }
            }
            is MessageHolder -> {
                messageError?.let { holder.bind(it) }

                holder.itemView.setOnClickListener {
                    listener.onGetDataTryAgain()
                }
            }
        }
    }

    private class MessageHolder(view: View) : BaseViewHolder<String>(view) {

        override fun bind(obj: String) {
            val mMessage = itemView.txtMessage

            itemView.btnTryAgain.visibility = View.GONE

            if (obj.isEmpty()) {
                itemView.imgIcon.setImageResource(R.drawable.ic_default_product_empty)
                itemView.txtMessage.text = itemView.context.getString(R.string.khong_co_san_pham_nao)
            } else {
                when (obj) {
                    itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai) -> {
                        itemView.imgIcon.setImageResource(R.drawable.ic_error_network)
                        itemView.txtMessage.text = obj
                    }
                    itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai) -> {
                        itemView.imgIcon.setImageResource(R.drawable.ic_error_request)
                        itemView.txtMessage.text = obj
                    }
                    else -> {
                        itemView.txtMessage.text = obj
                    }
                }
            }
            mMessage.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray3))
        }
    }
}
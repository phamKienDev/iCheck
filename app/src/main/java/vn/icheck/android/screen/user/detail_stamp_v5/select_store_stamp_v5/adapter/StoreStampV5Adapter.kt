package vn.icheck.android.screen.user.detail_stamp_v5.select_store_stamp_v5.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_store_stamp_v6.view.*
import kotlinx.android.synthetic.main.layout_product_barcode_message.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.detail_stamp_v6.ICObjectStoreV6
import vn.icheck.android.screen.user.detail_stamp_v5.select_store_stamp_v5.view.ISelectStoreStampV5View
import vn.icheck.android.screen.user.detail_stamp_v6.select_store_stamp_v6.view.ISelectStoreStampV6View

class StoreStampV5Adapter(val view: ISelectStoreStampV5View) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemType = -1
    private val emptyType = -2
    private var errorCode = ""
    var isLoadMore = true
    val listData = mutableListOf<ICObjectStoreV6>()

    fun setListData(list: MutableList<ICObjectStoreV6>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setError(error: String) {
        listData.clear()
        errorCode = error
        isLoadMore = false
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_store_stamp_v6, parent, false))
            else -> ErrorHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_product_barcode_message, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else
            1
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty())
            itemType
        else
            emptyType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val item = listData[position]
                holder.setData(item)

                holder.itemView.setOnClickListener {
                    view.onClickItem(item)
                }
            }

            is ErrorHolder -> {
                holder.setData(errorCode)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(item: ICObjectStoreV6) {
            itemView.tvNameStore.text = item.name
        }
    }

    class ErrorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setData(errorCode: String) {
            itemView.txtMessage.text = if (errorCode.isNotEmpty())
                errorCode
            else
                itemView.context.getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)
        }
    }

}
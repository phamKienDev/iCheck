package vn.icheck.android.loyalty.screen.select_address.province

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_address.view.*
import kotlinx.android.synthetic.main.item_message_loyalty_point.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.BaseViewHolder
import vn.icheck.android.loyalty.helper.TextHelper
import vn.icheck.android.loyalty.model.ICProvince
import vn.icheck.android.loyalty.screen.select_address.ISelectAddressListener

class SelectProvinceAdapter(val listener: ISelectAddressListener<ICProvince>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICProvince>()
    private val listSearch = mutableListOf<ICProvince>()

    private var errorMessage: String? = null

    private val itemType = 1
    private val messageType = 2

    fun setData(list: MutableList<ICProvince>) {
        errorMessage = ""

        listData.clear()
        listSearch.clear()

        listData.addAll(list)
        listSearch.addAll(listData)

        notifyDataSetChanged()
    }

    fun addData(list: MutableList<ICProvince>) {
        errorMessage = ""

        listData.addAll(list)
        listSearch.addAll(listData)

        notifyDataSetChanged()
    }

    fun setError(error: String) {
        listData.clear()
        listSearch.clear()
        errorMessage = error
        notifyDataSetChanged()
    }

    val isListNotEmpty: Boolean
        get() {
            return listSearch.isNotEmpty()
        }

    fun search(key: String) {
        if (key.isNotEmpty()) {
            val mKey = TextHelper.unicodeToKoDauLowerCase(key)
            val listFilter = listData.filter { it.searchKey.contains(mKey) }
            listSearch.clear()
            listSearch.addAll(listFilter)
        } else {
            listSearch.clear()
            listSearch.addAll(listData)
        }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (listSearch.isNotEmpty()) {
            listSearch.size
        } else {
            if (errorMessage != null) {
                1
            } else {
                0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listSearch.isNotEmpty()) {
            itemType
        } else {
            if (errorMessage != null) {
                messageType
            } else {
                super.getItemViewType(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(parent)
            else -> MessageHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val product = listSearch[position]

                holder.bind(product)

                holder.itemView.txtTitle.setOnClickListener {
                    listener.onItemClicked(product)
                }
            }
            is MessageHolder -> {
                errorMessage?.let {
                    holder.bind(it)
                }

                holder.itemView.setOnClickListener {
                    listener.onMessageClicked()
                }
            }
        }
    }

    private class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICProvince>(R.layout.item_address, parent) {

        override fun bind(obj: ICProvince) {
            itemView.txtTitle.text = obj.name
        }
    }

    private class MessageHolder(parent: ViewGroup) : BaseViewHolder<String>(R.layout.item_message_loyalty_point, parent) {

        override fun bind(obj: String) {
            itemView.tvMessageBottom.text = if (obj.isEmpty()) {
                itemView.context.getString(R.string.khong_tim_thay_ket_qua_phu_hop)
            } else {
                obj
            }
        }
    }
}
package vn.icheck.android.screen.user.selectward.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_address.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICWard
import vn.icheck.android.screen.user.selectward.view.SelectWardView

class SelectWardAdapter(val listener: SelectWardView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICWard>()
    private val listSearch = mutableListOf<ICWard>()

    private var errorMessage: String? = null

    private val itemType = 1
    private val messageType = 2

    fun setData(list: MutableList<ICWard>) {
        errorMessage = ""

        listData.clear()
        listSearch.clear()

        listData.addAll(list)
        listSearch.addAll(listData)

        notifyDataSetChanged()
    }

    fun addData(list: MutableList<ICWard>) {
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
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            itemType -> ViewHolder(layoutInflater.inflate(R.layout.item_address, parent, false))
            else -> MessageHolder(layoutInflater.inflate(R.layout.item_message, parent, false))
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

    private class ViewHolder(view: View) : BaseViewHolder<ICWard>(view) {

        override fun bind(obj: ICWard) {
            itemView.txtTitle.text = obj.name
        }
    }

    private inner class MessageHolder(view: View) : BaseViewHolder<String>(view) {

        override fun bind(obj: String) {
            itemView.txtMessage.text = if (obj.isEmpty()) {
                if (listData.isEmpty()) {
                    itemView.context.getString(R.string.khong_co_phuong_xa_nao)
                } else {
                    itemView.context.getString(R.string.khong_tim_thay_ket_qua_phu_hop)
                }
            } else {
                obj
            }
        }
    }
}
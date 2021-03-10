package vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_address.view.*
import kotlinx.android.synthetic.main.item_message.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.room.entity.ICDistrict
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.bottom_sheet.view.ISelectDistrictView

class SelectDistrictBottomSheetAdapter(val listener: ISelectDistrictView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICDistrict>()
    private val listSearch = mutableListOf<ICDistrict>()

    private var errorMessage: String? = null

    private val itemType = 1
    private val messageType = 2

    fun setData(list: MutableList<ICDistrict>) {
        errorMessage = ""
        listData.clear()
        listData.addAll(list)
        listSearch.addAll(listData)
        notifyDataSetChanged()
    }

    fun setError(error: String?) {
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
            val listFilter = listData.filter { it.searchName.contains(mKey) }
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

    private class ViewHolder(view: View) : BaseViewHolder<ICDistrict>(view) {

        override fun bind(obj: ICDistrict) {
            itemView.txtTitle.text = obj.name
        }
    }

    private class MessageHolder(view: View) : BaseViewHolder<String>(view) {

        override fun bind(obj: String) {
            itemView.txtMessage.text = if (obj.isEmpty()) {
                itemView.context.getString(R.string.khong_tim_thay_ket_qua_phu_hop)
            } else {
                obj
            }
        }
    }
}
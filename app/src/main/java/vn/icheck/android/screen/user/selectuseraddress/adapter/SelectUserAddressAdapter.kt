package vn.icheck.android.screen.user.selectuseraddress.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_select_user_address.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.ICAddress
import vn.icheck.android.screen.user.campaign.holder.base.LongMessageHolder
import vn.icheck.android.screen.user.selectuseraddress.view.ISelectUserAddressView

class SelectUserAddressAdapter(val listener: ISelectUserAddressView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICAddress?>()
    private var addressID: Long? = null

    private val itemType = 1
    private val addType = 2
    private val messageType = 3

    private var icon: Int = 0
    private var message: String = ""
    private var button: Int? = null

    fun setAddressID(addressID: Long?) {
        this.addressID = addressID
    }

    fun setListData(list: MutableList<ICAddress>) {
        listData.clear()
        listData.addAll(list)
        listData.add(null)
        notifyDataSetChanged()
    }

    fun addTopItem(address: ICAddress) {
        if (listData.isEmpty()) {
            listData.add(address)
        } else {
            listData.add(0, address)
        }

        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<ICAddress>) {
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setMessage(icon: Int, message: String, button: Int?) {
        this.icon = icon
        this.message = message
        this.button = button
    }

    fun setError(icon: Int, message: String, button: Int?) {
        listData.clear()
        setMessage(icon, message, button)
        notifyDataSetChanged()
    }

    fun deleteItem(id: Long) {
        for (it in listData) {
            if (it?.id == id) {
                listData.remove(it)

                if (id == addressID) {
                    if (listData.size > 1) {
                        addressID = listData[0]?.id
                    } else {
                        addressID = null
                    }
                }

                notifyDataSetChanged()
                return
            }
        }
    }

    val getSelectedItem: ICAddress?
        get() {
            for (it in listData) {
                if (it?.id == addressID) {
                    return it
                }
            }

            return null
        }

    val isEmpty: Boolean
        get() {
            return listData.isEmpty()
        }

    override fun getItemCount(): Int {
        return if (listData.isNotEmpty()) {
            listData.size
        } else {
            if (message.isNotEmpty()) {
                1
            } else {
                0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (listData[position] != null) {
                itemType
            } else {
                addType
            }
        } else {
            if (message.isNotEmpty()) {
                messageType
            } else {
                super.getItemViewType(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_select_user_address, parent, false))
            addType -> AddHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_add_user_address, parent, false))
            else -> LongMessageHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                listData[position]?.let {
                    holder.bind(it)
                }
            }
            is AddHolder -> {
                holder.bind()
            }
            is LongMessageHolder -> {
                holder.bind(icon, message, button)

//                holder.listener(View.OnClickListener {
//                    listener.onMessageClicked()
//                })
            }
        }
    }

    private inner class ViewHolder(view: View) : BaseViewHolder<ICAddress>(view) {

        override fun bind(obj: ICAddress) {
            if (obj.id == addressID) {
                itemView.tvChecked.setImageResource(R.drawable.ic_radio_checked_blue_24dp)
                itemView.tvName.setTextColor(vn.icheck.android.ichecklibs.Constant.getSecondaryColor(itemView.context))
                itemView.tvNote.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkG1ray1))
            } else {
                itemView.tvChecked.setImageResource(R.drawable.ic_radio_un_checked_gray_24dp)
                itemView.tvName.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray3))
                itemView.tvNote.setTextColor(ContextCompat.getColor(itemView.context, R.color.darkGray3))
            }

            val address = obj.address + "\n" +
                    obj.ward?.name + ", " + obj.district?.name + "\n" +
                    obj.city?.name + ", " + obj.country?.name

            itemView.tvName.text = ("${obj.last_name} ${obj.first_name} - ${obj.phone}")
            itemView.tvNote.text = address

            itemView.imgClose.setOnClickListener {
                obj.id?.let { id ->
                    listener.onDeleteAddress(id)
                }
            }

            itemView.setOnClickListener {
                addressID = obj.id
                notifyDataSetChanged()
            }
        }
    }

    private inner class AddHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            itemView.setOnClickListener {
                listener.onAddUserAddress()
            }
        }
    }
}
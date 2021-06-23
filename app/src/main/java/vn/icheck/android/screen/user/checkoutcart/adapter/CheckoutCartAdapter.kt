package vn.icheck.android.screen.user.checkoutcart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.LongMessageHolder
import vn.icheck.android.screen.user.checkoutcart.entity.Checkout
import vn.icheck.android.screen.user.checkoutcart.holder.*
import vn.icheck.android.screen.user.checkoutcart.view.ICheckoutCartView

class CheckoutCartAdapter(val listener: ICheckoutCartView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<Checkout>()

    private val loadingType = 5
    private val messageType = 6

    private var icon: Int = 0
    private var message: String = ""
    private var button: Int? = null

    companion object {
        val titleType = 1
        val addressType = 2
        val addAddressType = 3
        val orderType = 4
        val paymentType = 5
        val moneyType = 6
    }

    fun setListData(list: MutableList<Checkout>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addListData(list: MutableList<Checkout>) {
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

    val isEmpty: Boolean
        get() {
            return listData.isEmpty()
        }

    val getListData: MutableList<Checkout>
        get() {
            return listData
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
        return if (position < listData.size) {
            listData[position].type
        } else {
            if (listData.isNotEmpty()) {
                loadingType
            } else if (message.isNotEmpty()) {
                messageType
            } else {
                super.getItemViewType(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            titleType -> CheckoutTitleHolder(inflater.inflate(R.layout.item_checkout_title, parent, false), listener)
            addressType -> CheckoutAddressHolder(inflater.inflate(R.layout.item_checkout_address, parent, false))
            addAddressType -> CheckoutAddAddressHolder(inflater.inflate(R.layout.item_checkout_add_address, parent, false), listener)
            orderType -> CheckoutOrderHolder(inflater.inflate(R.layout.item_checkout_order, parent, false), listener)
            paymentType -> CheckoutPaymentHolder(inflater.inflate(R.layout.item_checkout_payment, parent, false), listener)
            moneyType -> CheckoutMoneyHolder(inflater.inflate(R.layout.item_checkout_money, parent, false))
            else -> LongMessageHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CheckoutTitleHolder -> {
                holder.bind(listData[position])
            }
            is CheckoutAddressHolder -> {
                listData[position].address?.let {
                    holder.bind(it)
                }
            }
            is CheckoutAddAddressHolder -> {
                holder.bind(null)
            }
            is CheckoutOrderHolder -> {
                listData[position].order?.let {
                    holder.bind(it)
                }
            }
            is CheckoutPaymentHolder -> {
                holder.bind(listData[position])
            }
            is CheckoutMoneyHolder -> {
                holder.bind(listData[position])
            }
            is LongMessageHolder -> {
                holder.bind(icon, message, button)

                holder.setListener(View.OnClickListener {
                    listener.onMessageClicked()
                })
            }
        }
    }
}
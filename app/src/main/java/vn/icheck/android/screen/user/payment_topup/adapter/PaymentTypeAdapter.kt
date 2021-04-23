package vn.icheck.android.screen.user.payment_topup.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_payment_type.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TextColorHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.base.SettingManager
import vn.icheck.android.network.models.ICPaymentType
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.screen.user.payment_topup.PaymentTopupActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class PaymentTypeAdapter(val context: Context?, val listener: ItemClickListener<ICRechargePhone>) : RecyclerView.Adapter<PaymentTypeAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICRechargePhone>()
    private var selectedPosition = -1

    fun setListData(list: MutableList<ICRechargePhone>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    val selectedItem: ICRechargePhone?
        get() {
            return if (selectedPosition != -1) {
                listData[selectedPosition]
            } else {
                null
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_payment_type, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            when (listData[position].agent?.code) {
                "ICHECK-XU" -> {
                    listener.onItemClick(1, listData[position])
                }
                else -> {
                    listener.onItemClick(2, listData[position])
                }
            }
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ICRechargePhone) {
            if (selectedPosition == -1) {
                listener.onItemClick(1, listData[adapterPosition])
                selectedPosition = 0
            }

            if (selectedPosition != adapterPosition) {
                itemView.radioButton1.setImageResource(R.drawable.ic_radio_un_checked_gray_24dp)
                itemView.tvNameType.setTextColor(Color.parseColor("#757575"))
                itemView.tvValue.setTextColor(Color.parseColor("#757575"))
            } else {
                itemView.radioButton1.setImageResource(R.drawable.ic_radio_on_24dp)
                itemView.tvNameType.setTextColor(TextColorHelper.getColorNormalText(itemView.context))
                itemView.tvValue.setTextColor(TextColorHelper.getColorNormalText(itemView.context))
            }

            WidgetUtils.loadImageUrlRounded(itemView.imgType, item.agent?.avatar, SizeHelper.size4)

            itemView.tvNameType.text = item.agent?.name

            when (item.agent?.code) {
                "ICHECK-XU" -> {
                    if (!SessionManager.isUserLogged) {
                        itemView.tvValue.text = "Số dư hiện tại: 0 Xu"
                    } else {
                        itemView.tvValue.text = "Số dư hiện tại: ${TextHelper.formatMoneyPhay(SessionManager.getCoin())} Xu"
                    }
                }
                else -> {
                    itemView.tvValue.visibility = View.GONE
                }
            }
        }
    }
}
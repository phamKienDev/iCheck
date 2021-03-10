package vn.icheck.android.screen.user.payment_topup.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_name_value_payment.view.*
import kotlinx.android.synthetic.main.item_service_shop_variant.view.*
import vn.icheck.android.R
import vn.icheck.android.model.ICNameValue

class ValuePaymentAdapter (val context: Context?) : RecyclerView.Adapter<ValuePaymentAdapter.ViewHolder>() {

    private val listData = mutableListOf<ICNameValue>()

    fun setListData(list: MutableList<ICNameValue>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_name_value_payment,parent,false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        holder.bind(item)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ICNameValue) {
            itemView.tvName.text = item.name
            itemView.tvValue.text = item.value
        }
    }

}
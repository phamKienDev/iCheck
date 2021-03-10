package vn.icheck.android.screen.user.recharge_phone.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_network.view.*
import vn.icheck.android.R
import vn.icheck.android.callback.OnItemClickListener
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.util.kotlin.WidgetUtils

class NetworkAdapter (val recyclerView: RecyclerView, var listData: MutableList<ICRechargePhone>) : RecyclerView.Adapter<NetworkAdapter.ViewHolder>() {
    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_network, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]

        holder.bindData(item, listener)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(item: ICRechargePhone, listener: OnItemClickListener?) {
            WidgetUtils.loadImageFitCenterUrl(itemView.imgAvaNetwork,item.avatar)

            if (item.select){
                itemView.layoutParent.setBackgroundResource(R.drawable.bg_choose_card)
            }else{
                itemView.layoutParent.setBackgroundResource(R.drawable.bg_default_card_loyalty)
            }

            itemView.setOnClickListener {
                listener?.onItemClick(itemView, layoutPosition)
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}